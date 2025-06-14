package adridi.user_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupRepoResponse {
    private Long id;
    private String group_name;
    private String group_description;
    private String group_type;
    private OrganizationDTO organization;
    private Set<UserDTO> teachers;
}