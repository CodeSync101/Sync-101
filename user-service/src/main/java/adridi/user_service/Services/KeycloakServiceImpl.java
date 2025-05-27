package adridi.user_service.Services;

import adridi.user_service.DTO.UserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.ws.rs.core.Response;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.admin-client.username}")
    private String adminUsername;

    @Value("${keycloak.admin-client.password}")
    private String adminPassword;

    @Value("${keycloak.admin-client.client-id}")
    private String adminClientId;

    @Override
    public String createKeycloakUser(UserRequest userRequest) {
        try {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm("master")
                    .username(adminUsername)
                    .password(adminPassword)
                    .clientId(adminClientId)
                    .build();

            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setUsername(userRequest.getUsername());
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            user.setEmail(userRequest.getEmail());
            user.setEmailVerified(true);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(userRequest.getPassword());
            credential.setTemporary(false);
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
            log.debug("Created Keycloak user with ID: {}", userId);

            response.close();
            return userId;

        } catch (Exception e) {
            log.error("Error creating Keycloak user: {}", e.getMessage());
            throw new RuntimeException("Failed to create Keycloak user: " + e.getMessage());
        }
    }
}