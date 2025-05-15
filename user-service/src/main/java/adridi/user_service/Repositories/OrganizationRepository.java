package adridi.user_service.Repositories;

import adridi.user_service.Models.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository <Organization, Long> {
}
