package adridi.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwt -> jwt.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter())
                )
                );
        return http.build();
    }
}


//http
//        .csrf().disable()
//                .authorizeHttpRequests(authorize -> authorize
//        .requestMatchers("/api/v*/registration/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                        .formLogin()
//                .loginProcessingUrl("/api/v*/login")
//                .permitAll()
//                .defaultSuccessUrl("/api/v*/login/success", true)
//                .failureUrl("/api/v*/login/error");
//
//        return http.build();