package adridi.user_service.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "groups_map", uniqueConstraints = @UniqueConstraint(columnNames = "group_name"))
public class GroupRepo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String group_name;
    private String group_description;
    private String group_type;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToMany(mappedBy = "groups")
    private Set<User> users = new HashSet<>();

    @ManyToMany(mappedBy = "teacherGroups")
    private Set<User> teachers = new HashSet<>();

    public GroupRepo(String group_name, String group_description, String group_type) {
        this.group_name = group_name;
        this.group_description = group_description;
        this.group_type = group_type;
        this.users = new HashSet<>();
        this.teachers = new HashSet<>();
    }

    public GroupRepo(String group_name, String group_description, String group_type, Organization organization) {
        this.group_name = group_name;
        this.group_description = group_description;
        this.group_type = group_type;
        this.organization = organization;
        this.users = new HashSet<>();
        this.teachers = new HashSet<>();
    }
}