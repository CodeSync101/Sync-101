package adridi.user_service.Repositories;

import adridi.user_service.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKeycloakId(String keycloakId);

//    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'STUDENT'")
//    long countStudents();
//
//    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'TEACHER'")
//    long countTeachers();
}