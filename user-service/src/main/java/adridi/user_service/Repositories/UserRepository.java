package adridi.user_service.Repositories;

import adridi.user_service.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}