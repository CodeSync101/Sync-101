package adridi.user_service.Services;

import adridi.user_service.DTO.UserRequest;
import org.springframework.stereotype.Service;

@Service
public interface KeycloakService {
    String createKeycloakUser(UserRequest userRequest);
}