package esfot.tesis.botics.service;

import esfot.tesis.botics.entity.Ticket;
import esfot.tesis.botics.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketServiceImpl implements TicketService{
    @Autowired
    TicketRepository ticketRepository;

    @Override
    public List<Ticket> getTicketsByUserId(Long idUser) {
        return ticketRepository.findAllByUserId(idUser);
    }

    @Override
    public void storeTicket(Ticket ticket) {
        ticketRepository.save(ticket);
    }

    @Override
    public Ticket getTicketById(Long idTicket) {
        return ticketRepository.findByTicketId(idTicket);
    }

    @Override
    public List<Ticket> getTickets() {
        return ticketRepository.findAll();
    }
}
