package adridi.user_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupRepoDTO {
    private Long id;
    private String group_name;
    private String group_description;
    private String group_type;
}