package esfot.tesis.botics.controller;

import esfot.tesis.botics.auth.entity.User;
import esfot.tesis.botics.auth.payload.response.MessageResponse;
import esfot.tesis.botics.auth.repository.UserRepository;
import esfot.tesis.botics.entity.Reserve;
import esfot.tesis.botics.entity.Response;
import esfot.tesis.botics.entity.Ticket;
import esfot.tesis.botics.payload.request.ResponseRequest;
import esfot.tesis.botics.service.ReserveServiceImpl;
import esfot.tesis.botics.service.ResponseServiceImpl;
import esfot.tesis.botics.service.TicketServiceImpl;
import esfot.tesis.botics.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/intern")
public class InternController {
    @Autowired
    TicketServiceImpl ticketService;

    @Autowired
    ResponseServiceImpl responseService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    ReserveServiceImpl reserveService;

    @GetMapping("/manage/tickets")
    @PreAuthorize("hasRole('PASANTE')")
    public ResponseEntity<?> indexTickets() {
        List<Ticket> tickets = ticketService.getTickets();
        return ResponseEntity.ok().body(tickets);
    }

    @GetMapping("/manage/reserves")
    @PreAuthorize("hasRole('PASANTE')")
    public ResponseEntity<?> indexReserves() {
        List<Reserve> reserves = reserveService.getReserves();
        return ResponseEntity.ok().body(reserves);
    }

    @PostMapping("/manage/tickets/response/{idUser}/{idTicket}")
    @PreAuthorize("hasRole('PASANTE')")
    public ResponseEntity<?> responseTicket(@PathVariable("idUser") Long idUser, @PathVariable("idTicket") Long idTicket, @RequestBody ResponseRequest responseRequest) {
        Ticket ticket = ticketService.getTicketById(idTicket);
        User user = userService.getUserByUserId(idUser);
        Response response = new Response();
        response.setSubject(responseRequest.getSubject());
        response.setDetails(responseRequest.getDetails());
        response.setUser(user);
        responseService.storeResponse(response);
        user.getResponse().add(response);
        userRepository.save(user);
        ticket.setResponse(response);
        ticket.setState(true);
        ticketService.storeTicket(ticket);
        return ResponseEntity.ok().body(new MessageResponse("Ticket atendido con éxito."));
    }

    @PostMapping("/manage/reserves/response/{idUser}/{idReserve}")
    @PreAuthorize("hasRole('PASANTE')")
    public ResponseEntity<?> responseReserve(@PathVariable("idUser") Long idUser, @PathVariable("idReserve") Long idReserve, @RequestBody ResponseRequest responseRequest) {
        Reserve reserve = reserveService.getReserveById(idReserve);
        User user = userService.getUserByUserId(idUser);
        Response response = new Response();
        response.setSubject(responseRequest.getSubject());
        response.setDetails(responseRequest.getDetails());
        response.setUser(user);
        responseService.storeResponse(response);
        user.getResponse().add(response);
        userRepository.save(user);
        reserve.setResponse(response);
        reserve.setState(true);
        reserveService.storeReserve(reserve);
        return ResponseEntity.ok().body(new MessageResponse("Reserva atendido con éxito."));
    }
}
