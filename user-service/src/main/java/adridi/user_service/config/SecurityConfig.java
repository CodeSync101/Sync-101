package adridi.user_service.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users/register").permitAll()
                        .requestMatchers("/api/v1/users/all").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/{id}/groups/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/groups/teacher").hasRole("TEACHER")
                        .requestMatchers("/api/v1/users/organizations/field-manager").hasRole("FIELD_MANAGER")
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/organization/create").hasRole("ADMIN")
                        .requestMatchers("/api/v1/organization").hasAnyRole("ADMIN", "FIELD_MANAGER")
                        .requestMatchers("/api/v1/organization/{id}").hasAnyRole("ADMIN", "FIELD_MANAGER")
                        .requestMatchers("/api/v1/organization/{orgId}/classroom/{classRoomId}").hasRole("ADMIN")
                        .requestMatchers("/api/v1/group/create-group").hasRole("ADMIN")
                        .requestMatchers("/api/v1/group").hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers("/api/v1/group/{id}").hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access.roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}