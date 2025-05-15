package adridi.user_service.Repositories;

import adridi.user_service.Models.GroupRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface GroupRepoRepository extends JpaRepository<GroupRepo, Long> {
    @Query("SELECT g FROM GroupRepo g WHERE g.group_name = :groupName")
    Optional<GroupRepo> findByGroup_name(String groupName);
}