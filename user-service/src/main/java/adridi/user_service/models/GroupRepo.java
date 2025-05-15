package adridi.user_service.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    private String group_type; // (public, private)

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

    public GroupRepo(String group_name, String group_description, String group_type) {
        this.group_name = group_name;
        this.group_description = group_description;
        this.group_type = group_type;
    }

    public GroupRepo(String group_name, String group_description, String group_type, Organization organization) {
        this.group_name = group_name;
        this.group_description = group_description;
        this.group_type = group_type;
        this.organization = organization;
    }
}