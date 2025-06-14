package adridi.user_service.Services;

import adridi.user_service.DTO.UserRequest;
import adridi.user_service.Models.Role;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin-client.username}")
    private String adminUsername;

    @Value("${keycloak.admin-client.password}")
    private String adminPassword;

    @Value("${keycloak.admin-client.client-id}")
    private String clientId;

    @Value("${keycloak.admin-client.grant-type}")
    private String grantType;

    @Override
    public String createKeycloakUser(UserRequest userRequest) {
        Keycloak keycloak = null;
        try {
            // Build Keycloak client with admin credentials
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm("master") // Admin credentials are in 'master' realm
                    .grantType(grantType)
                    .clientId(clientId)
                    .username(adminUsername)
                    .password(adminPassword)
                    .build();
            log.debug("Keycloak client initialized for admin user: {}", adminUsername);

            // Create user in 'codesync-auth' realm
            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setUsername(userRequest.getUsername());
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            user.setEmail(userRequest.getEmail());
            user.setEmailVerified(true);

            String randomPassword = generateRandomPassword(12);
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(randomPassword);
            credential.setTemporary(true);
            user.setCredentials(Collections.singletonList(credential));

            Response response = keycloak.realm(realm).users().create(user);
            int status = response.getStatus();

            if (status != 201) {
                String errorMessage = response.readEntity(String.class);
                log.error("Failed to create Keycloak user. Status: {}, Error: {}", status, errorMessage);
                throw new RuntimeException("Failed to create Keycloak user: " + errorMessage);
            }

            String locationHeader = response.getHeaderString("Location");
            if (locationHeader == null) {
                throw new RuntimeException("No Location header in Keycloak response");
            }
            String userId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
            log.debug("Created Keycloak user with ID: {}, temporary password: {}", userId, randomPassword);

            if (userRequest.getRoles() != null && !userRequest.getRoles().isEmpty()) {
                List<RoleRepresentation> roleRepresentations = getRoleRepresentations(keycloak, userRequest.getRoles());
                keycloak.realm(realm).users().get(userId).roles().realmLevel().add(roleRepresentations);
                log.debug("Assigned roles {} to user {}", userRequest.getRoles(), userId);
            }

            response.close();
            return userId;

        } catch (Exception e) {
            log.error("Error creating Keycloak user: {}", e.getMessage());
            throw new RuntimeException("Failed to create Keycloak user: " + e.getMessage());
        } finally {
            if (keycloak != null) {
                keycloak.close();
                log.debug("Keycloak client closed");
            }
        }
    }

    private List<RoleRepresentation> getRoleRepresentations(Keycloak keycloak, Set<Role> roles) {
        return roles.stream()
                .map(Role::name)
                .map(role -> keycloak.realm(realm).roles().get(role).toRepresentation())
                .collect(Collectors.toList());
    }

    private String generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}