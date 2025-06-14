package adridi.user_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationDTO {
    private Long id;
    private String org_name;
    private String org_email;
    private String org_owner;
    private Long classRoomId;
    private List<GroupRepoDTO> groupRepos;
    private Set<UserDTO> fieldManagers;
}