package adridi.user_service.Services;

import adridi.user_service.DTO.UserRequest;
import adridi.user_service.DTO.UserUpdateRequest;
import adridi.user_service.Models.GroupRepo;
import adridi.user_service.Models.User;
import adridi.user_service.Repositories.GroupRepoRepository;
import adridi.user_service.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final GroupRepoRepository groupRepoRepository;
    private final PasswordEncoder passwordEncoder;
    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    private static final List<String> VALID_ROLES = Arrays.asList("admin", "student", "field-officer", "teacher");

    @Override
    @Transactional
    public User registerUser(UserRequest request) {
        // Validate group
        GroupRepo group = groupRepoRepository.findByGroup_name(request.getGroup_name())
                .orElseThrow(() -> new IllegalArgumentException("Group with name " + request.getGroup_name() + " not found"));

        // Validate role
        String role = request.getRole() != null ? request.getRole().toLowerCase() : "student";
        if (!VALID_ROLES.contains(role)) {
            throw new IllegalArgumentException("Invalid role: " + role + ". Valid roles are: " + VALID_ROLES);
        }

        // Validate role assignment
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!role.equals("student") && (auth == null || !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_admin")))) {
            throw new AccessDeniedException("Only admins can assign roles other than student");
        }

        // Check for existing user in Keycloak
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();
        List<UserRepresentation> existingUsers = usersResource.search(request.getUsername(), true);
        if (!existingUsers.isEmpty()) {
            throw new IllegalArgumentException("User with username " + request.getUsername() + " already exists in Keycloak");
        }

        // Create user in Keycloak
        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setUsername(request.getUsername());
        keycloakUser.setFirstName(request.getFirstName());
        keycloakUser.setLastName(request.getLastName());
        keycloakUser.setEmail(request.getEmail());
        keycloakUser.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        keycloakUser.setEmailVerified(false);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);
        keycloakUser.setCredentials(Collections.singletonList(credential));

        String userId;
        try (Response response = usersResource.create(keycloakUser)) {
            if (response.getStatus() == 409) {
                throw new IllegalArgumentException("User with username or email already exists in Keycloak");
            }
            if (response.getStatus() != 201) {
                String errorMessage = response.readEntity(String.class);
                throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatusInfo().getReasonPhrase() + " - " + errorMessage);
            }
            userId = response.getLocation() != null ? response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1") : null;
            if (userId == null) {
                throw new RuntimeException("Failed to extract Keycloak user ID from response");
            }
        } catch (WebApplicationException e) {
            throw new RuntimeException("Failed to create user in Keycloak: " + e.getMessage(), e);
        }

        // Assign role
        try {
            RoleRepresentation roleRepresentation = realmResource.roles().get(role).toRepresentation();
            usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(roleRepresentation));
        } catch (WebApplicationException e) {
            // Rollback Keycloak user creation
            try {
                usersResource.delete(userId);
            } catch (Exception rollbackEx) {
                System.err.println("Failed to rollback Keycloak user creation: " + rollbackEx.getMessage());
            }
            throw new RuntimeException("Failed to assign role in Keycloak: " + e.getMessage(), e);
        }

        // Create local user
        User user = new User(
                userId, // Store Keycloak user ID
                request.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getLocked() != null ? request.getLocked() : false,
                request.getEnabled() != null ? request.getEnabled() : true
        );
        user.setGroup(group);

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));

        // Delete from Keycloak
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();
        try {
            usersResource.delete(user.getKeycloakId());
        } catch (WebApplicationException e) {
            throw new RuntimeException("Failed to delete user in Keycloak: " + e.getMessage(), e);
        }

        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public User updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));

        // Update in Keycloak
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();
        UserRepresentation keycloakUser;
        try {
            keycloakUser = usersResource.get(user.getKeycloakId()).toRepresentation();
        } catch (WebApplicationException e) {
            throw new RuntimeException("Failed to find user in Keycloak: " + e.getMessage(), e);
        }

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            // Check for duplicate username
            List<UserRepresentation> existingUsers = usersResource.search(request.getUsername(), true);
            if (!existingUsers.isEmpty() && !existingUsers.get(0).getId().equals(user.getKeycloakId())) {
                throw new IllegalArgumentException("Username " + request.getUsername() + " is already taken");
            }
            user.setUsername(request.getUsername());
            keycloakUser.setUsername(request.getUsername());
        }
        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirst_name(request.getFirstName());
            keycloakUser.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLast_name(request.getLastName());
            keycloakUser.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
            keycloakUser.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(request.getPassword());
            credential.setTemporary(false);
            keycloakUser.setCredentials(Collections.singletonList(credential));
        }
        if (request.getGroup_name() != null && !request.getGroup_name().isBlank()) {
            GroupRepo group = groupRepoRepository.findByGroup_name(request.getGroup_name())
                    .orElseThrow(() -> new RuntimeException("Group with name " + request.getGroup_name() + " not found"));
            user.setGroup(group);
        }

        try {
            usersResource.get(user.getKeycloakId()).update(keycloakUser);
        } catch (WebApplicationException e) {
            throw new RuntimeException("Failed to update user in Keycloak: " + e.getMessage(), e);
        }

        return userRepository.save(user);
    }
}