package esfot.tesis.botics.repository;

import esfot.tesis.botics.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByUserId(Long idUser);

    @Query(value = "SELECT * FROM tickets WHERE id = ?1", nativeQuery = true)
    Ticket findByTicketId(Long idTicket);
}
