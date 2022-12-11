package esfot.tesis.botics.controller;

import esfot.tesis.botics.auth.entity.User;
import esfot.tesis.botics.auth.payload.response.MessageResponse;
import esfot.tesis.botics.auth.repository.UserRepository;
import esfot.tesis.botics.entity.Reserve;
import esfot.tesis.botics.entity.Response;
import esfot.tesis.botics.entity.Ticket;
import esfot.tesis.botics.payload.request.ResponseRequest;
import esfot.tesis.botics.payload.response.ReserveResponse;
import esfot.tesis.botics.payload.response.ResponseResponse;
import esfot.tesis.botics.payload.response.TicketResponse;
import esfot.tesis.botics.service.ReserveServiceImpl;
import esfot.tesis.botics.service.ResponseServiceImpl;
import esfot.tesis.botics.service.TicketServiceImpl;
import esfot.tesis.botics.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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
    @PreAuthorize("hasRole('PASANTE') or hasRole('ADMIN')")
    public ResponseEntity<?> indexTickets() {
        List<Ticket> tickets = ticketService.getTickets();
        if (tickets.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("No existen tickets registrados."));
        }else {
            List<TicketResponse> ticketResponses = new ArrayList<>();
            final String[] role = new String[1];
            final String[] role2 = new String[1];
            tickets.forEach(ticket -> {
                TicketResponse ticketResponse = new TicketResponse();
                ResponseResponse responseResponse = new ResponseResponse();
                ticketResponse.setId(ticket.getId());
                ticketResponse.setSubject(ticket.getSubject());
                ticketResponse.setDescription(ticket.getDescription());
                ticketResponse.setState(ticket.isState());
                ticketResponse.setFirstName(ticket.getUser().getFirstName());
                ticketResponse.setLastName(ticket.getUser().getLastName());
                ticketResponse.setEmail(ticket.getUser().getEmail());
                ticket.getUser().getRoles().forEach(role1 -> role[0] = role1.getName().toString());
                ticketResponse.setRole(role[0]);
                if (ticket.getResponse() == null) {
                    ticketResponse.setResponse(null);
                } else {
                    responseResponse.setId(ticket.getResponse().getId());
                    responseResponse.setSubject(ticket.getResponse().getSubject());
                    responseResponse.setDetails(ticket.getResponse().getDetails());
                    responseResponse.setFirstName(ticket.getResponse().getUser().getFirstName());
                    responseResponse.setLastName(ticket.getResponse().getUser().getLastName());
                    responseResponse.setEmail(ticket.getResponse().getUser().getEmail());
                    ticket.getResponse().getUser().getRoles().forEach( role3 -> role2[0] = role3.getName().toString());
                    responseResponse.setRole(role2[0]);
                    ticketResponse.setResponse(responseResponse);
                }
                ticketResponses.add(ticketResponse);
            });
            return ResponseEntity.ok().body(ticketResponses);
        }
    }

    @GetMapping("/manage/reserves")
    @PreAuthorize("hasRole('PASANTE') or hasRole('ADMIN')")
    public ResponseEntity<?> indexReserves() {
        List<Reserve> reserves = reserveService.getReserves();
        if (reserves.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen reservas registradas."));
        }else {
            List<ReserveResponse> reserveResponses = new ArrayList<>();
            final String[] role = new String[1];
            final String[] role2 = new String[1];
            reserves.forEach(reserve -> {
                ReserveResponse reserveResponse = new ReserveResponse();
                ResponseResponse responseResponse = new ResponseResponse();
                reserveResponse.setId(reserve.getId());
                reserveResponse.setLabName(reserve.getLabName());
                reserveResponse.setDescription(reserve.getDescription());
                reserveResponse.setState(reserve.isState());
                reserveResponse.setFirstName(reserve.getUser().getFirstName());
                reserveResponse.setLastName(reserve.getUser().getLastName());
                reserveResponse.setEmail(reserve.getUser().getEmail());
                reserve.getUser().getRoles().forEach(role1 -> role[0] = role1.getName().toString());
                reserveResponse.setRole(role[0]);
                if (reserve.getResponse() == null) {
                    reserveResponse.setResponse(null);
                } else {
                    responseResponse.setId(reserve.getResponse().getId());
                    responseResponse.setSubject(reserve.getResponse().getSubject());
                    responseResponse.setDetails(reserve.getResponse().getDetails());
                    responseResponse.setFirstName(reserve.getResponse().getUser().getFirstName());
                    responseResponse.setLastName(reserve.getResponse().getUser().getLastName());
                    responseResponse.setEmail(reserve.getResponse().getUser().getEmail());
                    reserve.getResponse().getUser().getRoles().forEach(role3 -> role2[0] = role3.getName().toString());
                    responseResponse.setRole(role2[0]);
                    reserveResponse.setResponse(responseResponse);
                }
                reserveResponses.add(reserveResponse);
            });
            return ResponseEntity.ok().body(reserveResponses);
        }
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
