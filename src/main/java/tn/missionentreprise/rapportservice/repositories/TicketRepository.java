package tn.missionentreprise.rapportservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.missionentreprise.rapportservice.Entity.Ticket;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query(value = "SELECT status, COUNT(*) FROM ticket GROUP BY status", nativeQuery = true)
    List<Object[]> countTicketsByStatus();
}