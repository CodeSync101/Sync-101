package adridi.user_service.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users_map")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String keycloakId;
    private String username;
    private String first_name;
    private String last_name;
    private String email;
    private Boolean locked;
    private Boolean enabled;
    private String githubUsername;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<GroupRepo> groups = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "teacher_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<GroupRepo> teacherGroups = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "field_manager_organizations",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "organization_id")
    )
    private Set<Organization> managedOrganizations = new HashSet<>();

    public User(Long id, String username, String first_name, String last_name,
                String email, Boolean locked, Boolean enabled) {
        this.id = id;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.locked = locked;
        this.enabled = enabled;
        this.groups = new HashSet<>();
        this.teacherGroups = new HashSet<>();
        this.managedOrganizations = new HashSet<>();
        this.roles = new HashSet<>();
    }
}