package esfot.tesis.botics.controller;

import esfot.tesis.botics.auth.entity.User;
import esfot.tesis.botics.auth.payload.response.MessageResponse;
import esfot.tesis.botics.auth.repository.UserRepository;
import esfot.tesis.botics.entity.Response;
import esfot.tesis.botics.entity.Ticket;
import esfot.tesis.botics.payload.request.CommentaryRequest;
import esfot.tesis.botics.payload.request.ResponseRequest;
import esfot.tesis.botics.payload.request.TicketRequest;
import esfot.tesis.botics.service.CommentaryServiceImpl;
import esfot.tesis.botics.entity.Commentary;
import esfot.tesis.botics.service.ResponseServiceImpl;
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
@RequestMapping("api/v1/administrative")
public class AdministrativeController {
    @Autowired
    CommentaryServiceImpl commentariesService;

    @Autowired
    TicketServiceImpl ticketService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    ResponseServiceImpl responseService;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/commentaries/{idUser}")
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public ResponseEntity<?> indexCommentariesByIdUser(@PathVariable("idUser") Long idUser) {
        List<Commentary> commentaries = commentariesService.getCommentariesByUserId(idUser);
        if (commentaries.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen comentarios registrados."));
        }else {
            return ResponseEntity.ok().body(commentaries);
        }
    }

    @PostMapping("/commentary/{idUser}")
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public ResponseEntity<?> storeCommentary(@RequestBody CommentaryRequest commentaryRequest, @PathVariable("idUser") Long idUser) {
        User currentUser = userService.getUserByUserId(idUser);
        Commentary commentary = new Commentary();
        commentary.setUser(currentUser);
        commentary.setSubject(commentaryRequest.getSubject());
        commentary.setMessage(commentaryRequest.getMessage());
        commentariesService.storeCommentary(commentary);
        return ResponseEntity.ok().body(new MessageResponse("Comentario guardado correctamente."));
    }

    @GetMapping("/tickets/{idUser}")
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public ResponseEntity<?> indexTicketsByIdUser(@PathVariable("idUser") Long idUser) {
        List<Ticket> tickets = ticketService.getTicketsByUserId(idUser);
        if (tickets.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen tickets registrados."));
        }else {
            return ResponseEntity.ok().body(tickets);
        }
    }

    @PostMapping("/ticket/{idUser}")
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public ResponseEntity<?> storeTicket(@RequestBody TicketRequest ticketRequest, @PathVariable("idUser") Long idUser) {
        User currentUser = userService.getUserByUserId(idUser);
        Ticket ticket = new Ticket();
        ticket.setUser(currentUser);
        ticket.setSubject(ticketRequest.getSubject());
        ticket.setDescription(ticketRequest.getDescription());
        ticketService.storeTicket(ticket);
        return ResponseEntity.ok().body(new MessageResponse("Ticket guardado correctamente."));
    }

    @GetMapping("/manage/commentaries/{idUser}")
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public ResponseEntity<?> indexCommentariesByNotId(@PathVariable("idUser") Long idUser) {
        List<Commentary> commentaries = commentariesService.getCommentariesByNotUserId(idUser);
        return ResponseEntity.ok().body(commentaries);
    }

    @PostMapping("/manage/commentaries/response/{idUser}/{idCommentary}")
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public ResponseEntity<?> responseCommentary(@PathVariable("idUser") Long idUser, @PathVariable("idCommentary") Long idCommentary, @RequestBody ResponseRequest responseRequest) {
        Commentary commentary = commentariesService.getCommentaryById(idCommentary);
        User user = userService.getUserByUserId(idUser);
        Response response = new Response();
        response.setDetails(responseRequest.getDetails());
        response.setSubject(responseRequest.getSubject());
        response.setUser(user);
        responseService.storeResponse(response);
        user.getResponse().add(response);
        userRepository.save(user);
        commentary.setResponse(response);
        commentary.setState(true);
        commentariesService.storeCommentary(commentary);
        return ResponseEntity.ok().body(new MessageResponse("Comentario atendido con éxito."));
    }

}
