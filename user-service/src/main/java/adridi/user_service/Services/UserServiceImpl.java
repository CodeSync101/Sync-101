package adridi.user_service.Services;

import adridi.user_service.DTO.UserRequest;
import adridi.user_service.DTO.UserUpdateRequest;
import adridi.user_service.Models.GroupRepo;
import adridi.user_service.Models.Organization;
import adridi.user_service.Models.Role;
import adridi.user_service.Models.User;
import adridi.user_service.Repositories.GroupRepoRepository;
import adridi.user_service.Repositories.OrganizationRepository;
import adridi.user_service.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final GroupRepoRepository groupRepoRepository;
    private final OrganizationRepository organizationRepository;
    private final GitHubService gitHubService;
    private final KeycloakService keycloakService;

    @Override
    @Transactional
    public User registerUser(UserRequest request) {
        String keycloakId = keycloakService.createKeycloakUser(request);

        User user = new User(
                null,
                request.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getLocked() != null ? request.getLocked() : false,
                request.getEnabled() != null ? request.getEnabled() : true
        );
        user.setKeycloakId(keycloakId);
        user.setGithubUsername(request.getGithubUsername());
        user.setGroups(new HashSet<>());
        user.setRoles(request.getRoles() != null ? request.getRoles() : new HashSet<>());

        if (request.getGroup_name() != null && !request.getGroup_name().isEmpty()) {
            GroupRepo group = groupRepoRepository.findByGroup_name(request.getGroup_name())
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            user.getGroups().add(group);
            group.getUsers().add(user);
        }

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public User updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getFirstName() != null) user.setFirst_name(request.getFirstName());
        if (request.getLastName() != null) user.setLast_name(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getLocked() != null) user.setLocked(request.getLocked());
        if (request.getEnabled() != null) user.setEnabled(request.getEnabled());

        if (request.getGroup_name() != null) {
            GroupRepo group = groupRepoRepository.findByGroup_name(request.getGroup_name())
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            user.getGroups().add(group);
            group.getUsers().add(user);
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User addUserToGroup(Long userId, Long groupId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupRepo group = groupRepoRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        try {
            String githubUsername = user.getGithubUsername();
            if (githubUsername == null || githubUsername.isEmpty()) {
                throw new RuntimeException("User does not have an associated GitHub username");
            }

            gitHubService.inviteUserToRepo(
                    group.getOrganization().getOrg_owner(),
                    group.getGroup_name(),
                    githubUsername,
                    "push"
            );

            user.getGroups().add(group);
            group.getUsers().add(user);

            return userRepository.save(user);
        } catch (Exception e) {
            log.error("Failed to add user to group: {}", e.getMessage());
            throw new RuntimeException("Failed to add user to group: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public User removeUserFromGroup(Long userId, String groupName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        GroupRepo group = groupRepoRepository.findByGroup_name(groupName)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        user.getGroups().remove(group);
        group.getUsers().remove(user);

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<GroupRepo> getUserGroups(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getGroups();
    }

    @Override
    @Transactional
    public User getOrCreateUserFromJwt() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String keycloakId = jwt.getSubject();

        List<String> roles = jwt.getClaimAsMap("realm_access") != null ?
                (List<String>) jwt.getClaimAsMap("realm_access").get("roles") : List.of();

        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setKeycloakId(keycloakId);
                    newUser.setUsername(jwt.getClaimAsString("preferred_username"));
                    newUser.setEmail(jwt.getClaimAsString("email"));
                    newUser.setFirst_name(jwt.getClaimAsString("given_name"));
                    newUser.setLast_name(jwt.getClaimAsString("family_name"));
                    newUser.setEnabled(true);
                    newUser.setLocked(false);
                    newUser.setGroups(new HashSet<>());
                    newUser.setTeacherGroups(new HashSet<>());
                    newUser.setManagedOrganizations(new HashSet<>());
                    newUser.setRoles(new HashSet<>());
                    return newUser;
                });

        Set<Role> userRoles = new HashSet<>();
        for (String role : roles) {
            try {
                userRoles.add(Role.valueOf(role.toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Ignoring invalid role: {}", role);
            }
        }
        user.setRoles(userRoles);
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<GroupRepo> getTeacherGroups() {
        User user = getOrCreateUserFromJwt();
        if (!user.getRoles().contains(Role.TEACHER)) {
            throw new SecurityException("User is not a teacher");
        }
        return user.getTeacherGroups();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Organization> getManagedOrganizations() {
        User user = getOrCreateUserFromJwt();
        if (!user.getRoles().contains(Role.FIELD_MANAGER)) {
            throw new SecurityException("User is not a field manager");
        }
        return user.getManagedOrganizations();
    }

    @Override
    @Transactional
    public void assignTeacherToGroup(Long userId, Long groupId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        GroupRepo group = groupRepoRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        if (!user.getRoles().contains(Role.TEACHER)) {
            throw new RuntimeException("User is not a teacher");
        }
        user.getTeacherGroups().add(group);
        group.getTeachers().add(user);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void assignFieldManagerToOrganization(Long userId, Long organizationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        if (!user.getRoles().contains(Role.FIELD_MANAGER)) {
            throw new RuntimeException("User is not a field manager");
        }
        user.getManagedOrganizations().add(organization);
        organization.getFieldManagers().add(user);
        userRepository.save(user);
    }
}