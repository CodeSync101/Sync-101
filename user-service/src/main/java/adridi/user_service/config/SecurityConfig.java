package adridi.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    public SecurityConfig(JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users/register").permitAll()
                        .requestMatchers("/api/v1/users/all").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/users/{id}/groups/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/users/groups/teacher").hasAuthority("TEACHER")
                        .requestMatchers("/api/v1/users/organizations/field-manager").hasAuthority("FIELD_MANAGER")
                        .requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/organization/create").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/organization").hasAnyAuthority("ADMIN", "FIELD_MANAGER")
                        .requestMatchers("/api/v1/organization/{id}").hasAnyAuthority("ADMIN", "FIELD_MANAGER")
                        .requestMatchers("/api/v1/organization/{orgId}/classroom/{classRoomId}").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/group/create-group").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/group").hasAnyAuthority("ADMIN", "TEACHER")
                        .requestMatchers("/api/v1/group/{id}").hasAnyAuthority("ADMIN", "TEACHER")
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/**").permitAll() // Allow OPTIONS for all /api/v1/** endpoints
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:4200");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}