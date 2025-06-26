package adridi.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class JwtAuthenticationConverterConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new Converter<Jwt, Collection<GrantedAuthority>>() {
            @Override
            public Collection<GrantedAuthority> convert(Jwt jwt) {
                Object realmAccess = jwt.getClaim("realm_access");
                if (realmAccess instanceof Map<?, ?> map) {
                    Object roles = map.get("roles");
                    if (roles instanceof List<?> roleList) {
                        return roleList.stream()
                                .filter(String.class::isInstance)
                                .map(role -> new SimpleGrantedAuthority(role.toString()))
                                .collect(Collectors.toList());
                    }
                }
                return Collections.emptyList();
            }
        });
        return converter;
    }
}