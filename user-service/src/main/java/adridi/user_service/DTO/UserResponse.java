package adridi.user_service.DTO;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String keycloakId;
    private String username;
    private String first_name;
    private String last_name;
    private String email;
    private Boolean locked;
    private Boolean enabled;
    private String githubUsername;
    private Set<GroupRepoDTO> groups;
}