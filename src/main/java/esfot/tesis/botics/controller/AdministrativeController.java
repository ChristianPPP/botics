package esfot.tesis.botics.controller;

import esfot.tesis.botics.auth.entity.User;
import esfot.tesis.botics.auth.payload.response.ErrorResponse;
import esfot.tesis.botics.auth.payload.response.MessageResponse;
import esfot.tesis.botics.auth.repository.UserRepository;
import esfot.tesis.botics.entity.Response;
import esfot.tesis.botics.entity.Ticket;
import esfot.tesis.botics.payload.request.CommentaryRequest;
import esfot.tesis.botics.payload.request.ResponseRequest;
import esfot.tesis.botics.payload.request.TicketRequest;
import esfot.tesis.botics.payload.response.CommentaryResponse;
import esfot.tesis.botics.payload.response.ResponseResponse;
import esfot.tesis.botics.payload.response.TicketResponse;
import esfot.tesis.botics.service.CommentaryServiceImpl;
import esfot.tesis.botics.entity.Commentary;
import esfot.tesis.botics.service.ResponseServiceImpl;
import esfot.tesis.botics.service.TicketServiceImpl;
import esfot.tesis.botics.service.UserServiceImpl;
import esfot.tesis.botics.validator.CommentaryValidator;
import esfot.tesis.botics.validator.ResponseValidator;
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
@RequestMapping("api/v1/administrative")
public class AdministrativeController {
    private final CommentaryServiceImpl commentariesService;

    private final TicketServiceImpl ticketService;

    private final UserServiceImpl userService;

    private final ResponseServiceImpl responseService;

    private final UserRepository userRepository;

    private final CommentaryValidator commentaryValidator;

    private final TicketValidator ticketValidator;

    private final ResponseValidator responseValidator;

    @Autowired
    public AdministrativeController(CommentaryServiceImpl commentariesService, TicketServiceImpl ticketService, UserServiceImpl userService, ResponseServiceImpl responseService, UserRepository userRepository, CommentaryValidator commentaryValidator, TicketValidator ticketValidator, ResponseValidator responseValidator) {
        this.commentariesService = commentariesService;
        this.ticketService = ticketService;
        this.userService = userService;
        this.responseService = responseService;
        this.userRepository = userRepository;
        this.commentaryValidator = commentaryValidator;
        this.ticketValidator = ticketValidator;
        this.responseValidator = responseValidator;
    }


    @Operation(summary = "Endpoint para listar los comentarios registrados por el usuario especificado.", description = "Retorna un arreglo de comentarios realizados por el usuario especificado por su id, cual ha sido su respuesta y que usuario dio dicha respuesta.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá la lista de comentarios según el usuario y sus respuestas o un mensaje si no existen registros.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/commentary/index/{idUser}")
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public ResponseEntity<?> indexCommentariesByIdUser(@PathVariable("idUser") Long idUser) {
        List<Commentary> commentaries = commentariesService.getCommentariesByUserId(idUser);
        if (commentaries.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen comentarios registrados."));
        }else {
            List<CommentaryResponse> commentaryResponses = new ArrayList<>();
            final String[] role = new String[1];
            final String[] role2 = new String[1];
            commentaries.forEach( commentary -> {
                CommentaryResponse commentaryResponse = new CommentaryResponse();
                ResponseResponse responseResponse = new ResponseResponse();
                commentaryResponse.setId(commentary.getId());
                commentaryResponse.setSubject(commentary.getSubject());
                commentaryResponse.setMessage(commentary.getMessage());
                commentaryResponse.setState(commentary.isState());
                commentaryResponse.setFirstName(commentary.getUser().getFirstName());
                commentaryResponse.setLastName(commentary.getUser().getLastName());
                commentaryResponse.setEmail(commentary.getUser().getEmail());
                commentary.getUser().getRoles().forEach(role1 -> role[0] = role1.getName().toString());
                commentaryResponse.setRole(role[0]);
                if (commentary.getResponse() == null) {
                    commentaryResponse.setResponse(null);
                } else {
                    responseResponse.setId(commentary.getResponse().getId());
                    responseResponse.setSubject(commentary.getResponse().getSubject());
                    responseResponse.setDetails(commentary.getResponse().getDetails());
                    responseResponse.setFirstName(commentary.getResponse().getUser().getFirstName());
                    responseResponse.setLastName(commentary.getResponse().getUser().getLastName());
                    responseResponse.setEmail(commentary.getResponse().getUser().getEmail());
                    commentary.getResponse().getUser().getRoles().forEach(role3 -> role2[0] = role3.getName().toString());
                    responseResponse.setRole(role2[0]);
                    commentaryResponse.setResponse(responseResponse);
                }
                commentaryResponses.add(commentaryResponse);
            });
            return ResponseEntity.ok().body(commentaryResponses);
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
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public ResponseEntity<?> storeCommentary(@RequestBody CommentaryRequest commentaryRequest, @PathVariable("idUser") Long idUser, BindingResult bindingResult) {
        User currentUser = userService.getUserByUserId(idUser);
        List<String> errors = new ArrayList<>();
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        commentaryValidator.validate(commentaryRequest, bindingResult);
        if (currentUser == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("El administrativo con id "+idUser+" no se encuentra registrado."));
        }
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
            return ResponseEntity.badRequest().body(new ErrorResponse("Error en el formulario.",errors));
        }
        Commentary commentary = new Commentary();
        commentary.setUser(currentUser);
        commentary.setSubject(commentaryRequest.getSubject());
        commentary.setMessage(commentaryRequest.getMessage());
        commentariesService.storeCommentary(commentary);
        return ResponseEntity.ok().body(new MessageResponse("Comentario guardado correctamente."));
    }


    @Operation(summary = "Endpoint para listar los tickets de asistencia registrados por el usuario especificado.", description = "Retorna un arreglo de los tickets de asistencia realizados por el usuario especificado por su id, cual ha sido su respuesta y que usuario dio dicha respuesta.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá la lista de los tickets según el usuario y sus respuestas o un mensaje si no existen registros.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/ticket/index/{idUser}")
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public ResponseEntity<?> indexTicketsByIdUser(@PathVariable("idUser") Long idUser) {
        List<Ticket> tickets = ticketService.getTicketsByUserId(idUser);
        if (tickets.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen tickets registrados."));
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


    @Operation(summary = "Endpoint para registrar un ticket de asistencia.", description = "Se envía un tickets de asistencia según el id del usuario para ser respondido por parte de un usuario con rol 'pasante' o 'administrativo'.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se retorna un mensaje que indica que el ticket de asistencia ha sido registrado exitosamente.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica errores en el formulario o bien que el usuario con el id especificado no se encuentra registrado.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/ticket/{idUser}")
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public ResponseEntity<?> storeTicket(@RequestBody TicketRequest ticketRequest, @PathVariable("idUser") Long idUser, BindingResult bindingResult) {
        User currentUser = userService.getUserByUserId(idUser);
        List<String> errors = new ArrayList<>();
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        ticketValidator.validate(ticketRequest, bindingResult);
        if (currentUser == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("El administrativo con id "+idUser+" no se encuentra registrado."));
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


    @Operation(summary = "Endpoint para listar los comentarios no registrados por el usuario especificado.", description = "Retorna un arreglo de comentarios no realizados por el usuario especificado por su id, cual ha sido la respuesta a cada uno y que usuario dio dicha respuesta.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá la lista de los comentarios que no han sido registrados por el usuario especificado según su id y sus respuestas o un mensaje si no existen registros.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/manage/commentary/index/{idUser}")
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public ResponseEntity<?> indexCommentariesByNotId(@PathVariable("idUser") Long idUser) {
        List<Commentary> commentaries = commentariesService.getCommentariesByNotUserId(idUser);
        if (commentaries.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen comentarios registrados."));
        }else {
            List<CommentaryResponse> commentaryResponses = new ArrayList<>();
            final String[] role = new String[1];
            final String[] role2 = new String[1];
            commentaries.forEach( commentary -> {
                CommentaryResponse commentaryResponse = new CommentaryResponse();
                ResponseResponse responseResponse = new ResponseResponse();
                commentaryResponse.setId(commentary.getId());
                commentaryResponse.setSubject(commentary.getSubject());
                commentaryResponse.setMessage(commentary.getMessage());
                commentaryResponse.setState(commentary.isState());
                commentaryResponse.setFirstName(commentary.getUser().getFirstName());
                commentaryResponse.setLastName(commentary.getUser().getLastName());
                commentaryResponse.setEmail(commentary.getUser().getEmail());
                commentary.getUser().getRoles().forEach(role1 -> role[0] = role1.getName().toString());
                commentaryResponse.setRole(role[0]);
                if (commentary.getResponse() == null) {
                    commentaryResponse.setResponse(null);
                } else {
                    responseResponse.setId(commentary.getResponse().getId());
                    responseResponse.setSubject(commentary.getResponse().getSubject());
                    responseResponse.setDetails(commentary.getResponse().getDetails());
                    responseResponse.setFirstName(commentary.getResponse().getUser().getFirstName());
                    responseResponse.setLastName(commentary.getResponse().getUser().getLastName());
                    responseResponse.setEmail(commentary.getResponse().getUser().getEmail());
                    commentary.getResponse().getUser().getRoles().forEach(role3 -> role2[0] = role3.getName().toString());
                    responseResponse.setRole(role2[0]);
                    commentaryResponse.setResponse(responseResponse);
                }
                commentaryResponses.add(commentaryResponse);
            });
            return ResponseEntity.ok().body(commentaryResponses);
        }
    }


    @Operation(summary = "Endpoint para responder a un comentario o sugerencia.", description = "Se da respuesta a un comentario o sugerencia realizado por otro usuario según el id del usuario que da la respuesta y según el id del comentario.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se retorna un mensaje que indica que el comentario ha sido atendido exitosamente o bien un mensaje que indica que el comentario no puede ser atendido.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica errores en el formulario.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/manage/commentary/response/{idUser}/{idCommentary}")
    @PreAuthorize("hasRole('ADMINISTRATIVO')")
    public ResponseEntity<?> responseCommentary(@PathVariable("idUser") Long idUser, @PathVariable("idCommentary") Long idCommentary, @RequestBody ResponseRequest responseRequest, BindingResult bindingResult) {
        Commentary commentary = commentariesService.getCommentaryById(idCommentary);
        List<String> errors = new ArrayList<>();
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        responseValidator.validate(responseRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
            return ResponseEntity.badRequest().body(new ErrorResponse("Error en el formulario.",errors));
        }
        if (commentary.getUser().getId() == idUser) {
            return ResponseEntity.ok().body(new MessageResponse("No se puede atender a este comentario."));
        } else {
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
}
