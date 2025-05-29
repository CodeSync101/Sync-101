package feten.tn.projetmission.Controllers;
import feten.tn.projetmission.Entities.Tache;
import feten.tn.projetmission.Entities.Ticket;
import feten.tn.projetmission.Services.NoteService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@AllArgsConstructor
@CrossOrigin("*")
@RestController
@RequestMapping("/Ticket")
public class TicketController {

    @Autowired
    private NoteService noteService;

    @PostMapping("/addticket/{tacheId}")
    public ResponseEntity<Ticket> addNote(@PathVariable Long tacheId, @RequestBody Ticket ticket) {
        return ResponseEntity.ok(noteService.addOrUpdateTicketNote(tacheId, ticket));
    }

    @PutMapping("/updateticket/{tacheId}/{ticketId}")
    public ResponseEntity<Ticket> updateNote(@PathVariable Long tacheId, @PathVariable Long ticketId, @RequestBody Ticket ticket) {
        ticket.setId(ticketId);
        return ResponseEntity.ok(noteService.addOrUpdateTicketNote(tacheId, ticket));
    }

    @DeleteMapping("/deleteticket/{ticketId}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long ticketId) {
        noteService.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/getTickets/{tacheId}")
    public ResponseEntity<List<Ticket>> getTickets(@PathVariable Long tacheId) {
        return ResponseEntity.ok(noteService.getTicketsByTache(tacheId));
    }

    @GetMapping("/getNotesTaches/{matiereId}")
    public ResponseEntity<List<Tache>> getTaches(@PathVariable Long matiereId) {
        return ResponseEntity.ok(noteService.getTachesByMatiere(matiereId));
    }
}
