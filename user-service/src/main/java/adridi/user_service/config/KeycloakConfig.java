package adridi.user_service.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin-client.username}")
    private String adminUsername;

    @Value("${keycloak.admin-client.password}")
    private String adminPassword;

    @Value("${keycloak.admin-client.client-id}")
    private String adminClientId;

    @Value("${keycloak.admin-client.grant-type}")
    private String grantType;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(grantType)
                .username(adminUsername)
                .password(adminPassword)
                .clientId(adminClientId)
                .build();
    }
}