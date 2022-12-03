package esfot.tesis.botics.controller;


import esfot.tesis.botics.auth.entity.User;
import esfot.tesis.botics.auth.payload.response.MessageResponse;
import esfot.tesis.botics.entity.Commentary;
import esfot.tesis.botics.entity.Reserve;
import esfot.tesis.botics.entity.Ticket;
import esfot.tesis.botics.payload.request.CommentaryRequest;
import esfot.tesis.botics.payload.request.ReserveRequest;
import esfot.tesis.botics.payload.request.TicketRequest;
import esfot.tesis.botics.service.CommentaryServiceImpl;
import esfot.tesis.botics.service.ReserveServiceImpl;
import esfot.tesis.botics.service.TicketServiceImpl;
import esfot.tesis.botics.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/teacher")
public class TeacherController {
    @Autowired
    UserServiceImpl userService;

    @Autowired
    CommentaryServiceImpl commentaryService;

    @Autowired
    TicketServiceImpl ticketService;

    @Autowired
    ReserveServiceImpl reserveService;

    @GetMapping("/commentaries/{idUser}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<?> indexCommentariesByIdUser(@PathVariable("idUser") Long idUser) {
        List<Commentary> commentaries = commentaryService.getCommentariesByUserId(idUser);
        if (commentaries.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen comentarios registrados."));
        }else {
            return ResponseEntity.ok().body(commentaries);
        }
    }

    @PostMapping("/commentary/{idUser}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<?> storeCommentary(@RequestBody CommentaryRequest commentaryRequest, @PathVariable("idUser") Long idUser) {
        User currentUser = userService.getUserByUserId(idUser);
        Commentary commentary = new Commentary();
        commentary.setUser(currentUser);
        commentary.setSubject(commentaryRequest.getSubject());
        commentary.setMessage(commentaryRequest.getMessage());
        commentaryService.storeCommentary(commentary);
        return ResponseEntity.ok().body(new MessageResponse("Comentario guardado correctamente."));
    }

    @GetMapping("/tickets/{idUser}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<?> indexTicketsByIdUser(@PathVariable("idUser") Long idUser) {
        List<Ticket> tickets = ticketService.getTicketsByUserId(idUser);
        if (tickets.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen tickets registrados."));
        }else {
            return ResponseEntity.ok().body(tickets);
        }
    }

    @PostMapping("/ticket/{idUser}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<?> storeTicket(@RequestBody TicketRequest ticketRequest, @PathVariable("idUser") Long idUser) {
        User currentUser = userService.getUserByUserId(idUser);
        Ticket ticket = new Ticket();
        ticket.setUser(currentUser);
        ticket.setSubject(ticketRequest.getSubject());
        ticket.setDescription(ticketRequest.getDescription());
        ticketService.storeTicket(ticket);
        return ResponseEntity.ok().body(new MessageResponse("Ticket guardado correctamente."));
    }

    @GetMapping("/reserve/{idUser}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<?> indexReservesByIdUser(@PathVariable("idUser") Long idUser) {
        List<Reserve> reserves = reserveService.getReservesByUserId(idUser);
        if (reserves.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen reservas registradas."));
        }else {
            return ResponseEntity.ok().body(reserves);
        }
    }

    @PostMapping("/reserve/{idUser}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<?> storeReserve(@RequestBody ReserveRequest reserveRequest, @PathVariable("idUser") Long idUser) {
        User currentUser = userService.getUserByUserId(idUser);
        Reserve reserve = new Reserve();
        reserve.setUser(currentUser);
        reserve.setLabName(reserveRequest.getLabName());
        reserve.setDescription(reserveRequest.getDescription());
        reserveService.storeReserve(reserve);
        return ResponseEntity.ok().body(new MessageResponse("Reserva guardada correctamente."));
    }
}
