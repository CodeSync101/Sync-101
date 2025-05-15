package feten.tn.projetmission.Services;

import feten.tn.projetmission.Entities.Matiere;
import feten.tn.projetmission.Entities.Tache;
import feten.tn.projetmission.Entities.Ticket;
import feten.tn.projetmission.Repositories.MatiereRepository;
import feten.tn.projetmission.Repositories.TacheRepository;
import feten.tn.projetmission.Repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    @Autowired
    private TicketRepository ticketRepo;
    @Autowired private TacheRepository tacheRepo;
    @Autowired private MatiereRepository matiereRepo;

    public Ticket addOrUpdateTicketNote(Long tacheId, Ticket ticket) {
        Tache tache = tacheRepo.findById(tacheId)
                .orElseThrow(() -> new RuntimeException("Tache not found"));

        ticket.setTache(tache);
        Ticket saved = ticketRepo.save(ticket);

        updateTacheNote(tache);
        updateMatiereNote(tache.getMatiere());

        return saved;
    }

    public void deleteTicket(Long ticketId) {
        Ticket ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        Tache tache = ticket.getTache();
        ticketRepo.delete(ticket);

        updateTacheNote(tache);
        updateMatiereNote(tache.getMatiere());
    }

    private void updateTacheNote(Tache tache) {
        List<Ticket> tickets = ticketRepo.findAll().stream()
                .filter(t -> t.getTache().getId().equals(tache.getId()))
                .collect(Collectors.toList());

        float avg = (float) tickets.stream()
                .mapToDouble(Ticket::getNoteTicket)
                .average().orElse(0.0);

        tache.setNotetache(avg);
        tacheRepo.save(tache);
    }

    private void updateMatiereNote(Matiere matiere) {
        List<Tache> taches = tacheRepo.findAll().stream()
                .filter(t -> t.getMatiere().getId().equals(matiere.getId()))
                .collect(Collectors.toList());

        float avg = (float) taches.stream()
                .mapToDouble(Tache::getNotetache)
                .average().orElse(0.0);

        matiere.setNoteMatiere(avg);
        matiereRepo.save(matiere);
    }

    public List<Ticket> getTicketsByTache(Long tacheId) {
        return ticketRepo.findAll().stream()
                .filter(t -> t.getTache().getId().equals(tacheId))
                .collect(Collectors.toList());
    }

    public List<Tache> getTachesByMatiere(Long matiereId) {
        return tacheRepo.findAll().stream()
                .filter(t -> t.getMatiere().getId().equals(matiereId))
                .collect(Collectors.toList());
    }
}
