package esfot.tesis.botics.service;

import esfot.tesis.botics.entity.Ticket;

import java.util.List;

public interface TicketService {
    List<Ticket> getTicketsByUserId(Long idUser);
    void storeTicket(Ticket ticket);
    Ticket getTicketById(Long idTicket);
    List<Ticket> getTickets();
}
