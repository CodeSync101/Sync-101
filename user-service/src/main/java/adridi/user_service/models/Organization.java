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
@Table(name = "organizations_map")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String org_name;
    private String org_email;
    private String org_owner;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private List<GroupRepo> groupRepos = new ArrayList<>();


    public Organization(String org_name, String org_email, String org_owner) {
        this.org_name = org_name;
        this.org_email = org_email;
        this.org_owner = org_owner;
    }


}
