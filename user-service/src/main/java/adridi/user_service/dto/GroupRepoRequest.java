package adridi.user_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupRepoRequest {
    private String group_name;
    private String group_description;
    private String group_type; // (public, private)
    private Long organizationId;

}
