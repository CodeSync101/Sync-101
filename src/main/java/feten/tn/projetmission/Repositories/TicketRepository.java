package feten.tn.projetmission.Repositories;

import feten.tn.projetmission.Entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {}