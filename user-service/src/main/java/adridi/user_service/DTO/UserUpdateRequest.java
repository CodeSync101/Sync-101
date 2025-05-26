package adridi.user_service.DTO;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String username;
    private String firstName;
    private String lastName;
    @Email(message = "Email must be valid")
    private String email;
    private String password;
    private String group_name;
    private Boolean locked;
    private Boolean enabled;
}