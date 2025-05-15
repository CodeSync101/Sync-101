package adridi.user_service.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users_map")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keycloakId; // Added to store Keycloak user ID

    private String username;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private Boolean locked;
    private Boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private GroupRepo group;

    public User(String keycloakId, String username, String first_name, String last_name, String email, String password, Boolean locked, Boolean enabled) {
        this.keycloakId = keycloakId;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.locked = locked;
        this.enabled = enabled;
    }
}