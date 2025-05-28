package tn.missionentreprise.rapportservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.missionentreprise.rapportservice.Entity.Ticket;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Requête native PostgreSQL
    @Query(value = "SELECT status, COUNT(*) FROM ticket " +
            "WHERE (:startDate IS NULL OR lastupdated >= :startDate) " +
            "AND (:endDate IS NULL OR lastupdated <= :endDate) " +
            "AND (:userIds IS NULL OR assigneduser_id = ANY(:userIds)) " +
            "GROUP BY status", nativeQuery = true)
    List<Object[]> countTicketsByStatus(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        @Param("userIds") Long[] userIds);

    // Requête JPQL
    @Query("SELECT t FROM Ticket t " +
            "WHERE (:startDate IS NULL OR t.lastupdated >= :startDate) " +
            "AND (:endDate IS NULL OR t.lastupdated <= :endDate) " +
            "AND (:userIds IS NULL OR t.assigneduser_id IN :userIds)")
    List<Ticket> findByFilters(@Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate,
                               @Param("userIds") List<Long> userIds);

}
