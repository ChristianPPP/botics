package esfot.tesis.botics.controller;


import esfot.tesis.botics.auth.entity.User;
import esfot.tesis.botics.auth.payload.response.ErrorResponse;
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
import esfot.tesis.botics.validator.CommentaryValidator;
import esfot.tesis.botics.validator.ReserveValidator;
import esfot.tesis.botics.validator.TicketValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/teacher")
public class TeacherController {
    private final UserServiceImpl userService;

    private final CommentaryServiceImpl commentaryService;

    private final TicketServiceImpl ticketService;

    private final ReserveServiceImpl reserveService;

    private final CommentaryValidator commentaryValidator;

    private final TicketValidator ticketValidator;

    private final ReserveValidator reserveValidator;

    @Autowired
    public TeacherController(UserServiceImpl userService, CommentaryServiceImpl commentaryService, TicketServiceImpl ticketService, ReserveServiceImpl reserveService, CommentaryValidator commentaryValidator, TicketValidator ticketValidator, ReserveValidator reserveValidator) {
        this.userService = userService;
        this.commentaryService = commentaryService;
        this.ticketService = ticketService;
        this.reserveService = reserveService;
        this.commentaryValidator = commentaryValidator;
        this.ticketValidator = ticketValidator;
        this.reserveValidator = reserveValidator;
    }


    @Operation(summary = "Endpoint para listar los comentarios registrados por el usuario especificado.", description = "Retorna un arreglo de comentarios realizados por el usuario especificado por su id, cual ha sido su respuesta y que usuario dio dicha respuesta.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá la lista de comentarios según el usuario y sus respuestas o un mensaje si no existen registros.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/commentary/index/{idUser}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<?> indexCommentariesByIdUser(@PathVariable("idUser") Long idUser) {
        List<Commentary> commentaries = commentaryService.getCommentariesByUserId(idUser);
        if (commentaries.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen comentarios registrados."));
        }else {
            return ResponseEntity.ok().body(commentaries);
        }
    }

    @Operation(summary = "Endpoint para registrar un comentario o sugerencia.", description = "Se envía un comentario o sugerencia referente al estado de los laboratorios según el id del usuario para ser respondido por parte de un usuario con rol 'pasante' o 'administrativo'.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se retorna un mensaje que indica que el comentario ha sido registrado exitosamente.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica errores en el formulario o bien que el usuario con el id especificado no se encuentra registrado.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/commentary/{idUser}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<?> storeCommentary(@RequestBody CommentaryRequest commentaryRequest, @PathVariable("idUser") Long idUser, BindingResult bindingResult) {
        User currentUser = userService.getUserByUserId(idUser);
        List<String> errors = new ArrayList<>();
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        commentaryValidator.validate(commentaryRequest, bindingResult);
        if (currentUser == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("El docente con id "+idUser+" no se encuentra registrado."));
        }
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
            return ResponseEntity.badRequest().body(new ErrorResponse("Error en el formulario.",errors));
        }
        Commentary commentary = new Commentary();
        commentary.setUser(currentUser);
        commentary.setSubject(commentaryRequest.getSubject());
        commentary.setMessage(commentaryRequest.getMessage());
        commentaryService.storeCommentary(commentary);
        return ResponseEntity.ok().body(new MessageResponse("Comentario guardado correctamente."));
    }


    @Operation(summary = "Endpoint para listar los tickets de asistencia registrados por el usuario especificado.", description = "Retorna un arreglo de los tickets de asistencia realizados por el usuario especificado por su id, cual ha sido su respuesta y que usuario dio dicha respuesta.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá la lista de los tickets según el usuario y sus respuestas o un mensaje si no existen registros.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/ticket/index/{idUser}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<?> indexTicketsByIdUser(@PathVariable("idUser") Long idUser) {
        List<Ticket> tickets = ticketService.getTicketsByUserId(idUser);
        if (tickets.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen tickets registrados."));
        }else {
            return ResponseEntity.ok().body(tickets);
        }
    }

    @Operation(summary = "Endpoint para registrar un ticket de asistencia.", description = "Se envía un tickets de asistencia según el id del usuario para ser respondido por parte de un usuario con rol 'pasante'.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se retorna un mensaje que indica que el ticket de asistencia ha sido registrado exitosamente.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica errores en el formulario o bien que el usuario con el id especificado no se encuentra registrado.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/ticket/{idUser}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<?> storeTicket(@RequestBody TicketRequest ticketRequest, @PathVariable("idUser") Long idUser, BindingResult bindingResult) {
        User currentUser = userService.getUserByUserId(idUser);
        List<String> errors = new ArrayList<>();
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        ticketValidator.validate(ticketRequest, bindingResult);
        if (currentUser == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("El docente con id "+idUser+" no se encuentra registrado."));
        }
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
            return ResponseEntity.badRequest().body(new ErrorResponse("Error en el formulario.",errors));
        }
        Ticket ticket = new Ticket();
        ticket.setUser(currentUser);
        ticket.setSubject(ticketRequest.getSubject());
        ticket.setDescription(ticketRequest.getDescription());
        ticketService.storeTicket(ticket);
        return ResponseEntity.ok().body(new MessageResponse("Ticket guardado correctamente."));
    }


    @Operation(summary = "Endpoint para listar las reservas registradas por el usuario especificado.", description = "Retorna un arreglo de las reservas realizados por el usuario especificado por su id, cual ha sido su respuesta y que usuario dio dicha respuesta.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá la lista de las reservas según el usuario y sus respuestas o un mensaje si no existen registros.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/reserve/index/{idUser}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<?> indexReservesByIdUser(@PathVariable("idUser") Long idUser) {
        List<Reserve> reserves = reserveService.getReservesByUserId(idUser);
        if (reserves.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen reservas registradas."));
        }else {
            return ResponseEntity.ok().body(reserves);
        }
    }


    @Operation(summary = "Endpoint para registrar una reserva.", description = "Se envía una reserva según el id del usuario para ser respondido por parte de un usuario con rol 'pasante'.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se retorna un mensaje que indica que el ticket de asistencia ha sido registrado exitosamente.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica errores en el formulario o bien que el usuario con el id especificado no se encuentra registrado.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/reserve/{idUser}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<?> storeReserve(@RequestBody ReserveRequest reserveRequest, @PathVariable("idUser") Long idUser, BindingResult bindingResult) {
        User currentUser = userService.getUserByUserId(idUser);
        List<String> errors = new ArrayList<>();
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        reserveValidator.validate(reserveRequest, bindingResult);
        if (currentUser == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("El docente con id "+idUser+" no se encuentra registrado."));
        }
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
            return ResponseEntity.badRequest().body(new ErrorResponse("Error en el formulario.",errors));
        }
        Reserve reserve = new Reserve();
        reserve.setUser(currentUser);
        reserve.setLabName(reserveRequest.getLabName());
        reserve.setDescription(reserveRequest.getDescription());
        reserveService.storeReserve(reserve);
        return ResponseEntity.ok().body(new MessageResponse("Reserva guardada correctamente."));
    }
}
