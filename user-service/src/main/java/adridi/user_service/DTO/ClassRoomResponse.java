package adridi.user_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassRoomResponse {
    private Long id;
    private String name;
    private Set<OrganizationDTO> organizations;
}