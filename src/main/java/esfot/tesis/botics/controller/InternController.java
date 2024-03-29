package esfot.tesis.botics.controller;

import esfot.tesis.botics.auth.entity.User;
import esfot.tesis.botics.auth.payload.response.ErrorResponse;
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
import esfot.tesis.botics.validator.ResponseValidator;
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
@ApiResponses(value= {
        @ApiResponse(responseCode = "401", description = "Esta respuesta indica un fallo de autenticación, para los endpoints privados esto puede inidicar que el JWT token ha caducado y se requiere obtener un nuevo token.", content =
                {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
})
@RequestMapping("api/v1/intern")
public class InternController {
    private final TicketServiceImpl ticketService;

    private final ResponseServiceImpl responseService;

    private final UserRepository userRepository;

    private final UserServiceImpl userService;

    private final ReserveServiceImpl reserveService;

    private final ResponseValidator responseValidator;

    @Autowired
    public InternController(TicketServiceImpl ticketService, ResponseServiceImpl responseService, UserRepository userRepository, UserServiceImpl userService, ReserveServiceImpl reserveService, ResponseValidator responseValidator) {
        this.ticketService = ticketService;
        this.responseService = responseService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.reserveService = reserveService;
        this.responseValidator = responseValidator;
    }


    @Operation(summary = "Endpoint para listar los tickets de asistencia registrados.", description = "Retorna un arreglo de los tickets de asistencia con el detalle de que usuario los realizó, cual ha sido su respuesta y que usuario dio dicha respuesta.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá la lista de tickets de asistencia y sus respuestas o un mensaje si no existen registros.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/manage/ticket/index")
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
                ticketResponse.setCreatedAt(ticket.getCreatedAt());
                ticketResponse.setUpdatedAt(ticket.getUpdatedAt());
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
                    responseResponse.setCreatedAt(ticket.getResponse().getCreatedAt());
                    responseResponse.setUpdatedAt(ticket.getResponse().getUpdatedAt());
                    ticketResponse.setResponse(responseResponse);
                }
                ticketResponses.add(ticketResponse);
            });
            return ResponseEntity.ok().body(ticketResponses);
        }
    }


    @Operation(summary = "Endpoint para listar las reservas registradas.", description = "Retorna un arreglo de reservas con el detalle de que usuario las realizó, cual ha sido su respuesta y que usuario dio dicha respuesta.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá la lista de reservas y sus respuestas o un mensaje si no existen registros.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/manage/reserve/index")
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
                reserveResponse.setCreatedAt(reserve.getCreatedAt());
                reserveResponse.setUpdatedAt(reserve.getUpdatedAt());
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
                    reserveResponse.setCreatedAt(reserve.getResponse().getCreatedAt());
                    reserveResponse.setUpdatedAt(reserve.getResponse().getUpdatedAt());
                    reserveResponse.setResponse(responseResponse);
                }
                reserveResponses.add(reserveResponse);
            });
            return ResponseEntity.ok().body(reserveResponses);
        }
    }


    @Operation(summary = "Endpoint para atender un ticket de asistencia.", description = "Se da respuesta a un ticket de asistencia según el id del usuario y según el id del ticket de asistencia.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se retorna un mensaje que indica que el ticket de asistencia ha sido atendido exitosamente.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica errores en el formulario, o bien que el ticket o usuario especificados por su id no se encuentran registrados.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/manage/ticket/response/{idUser}/{idTicket}")
    @PreAuthorize("hasRole('PASANTE')")
    public ResponseEntity<?> responseTicket(@PathVariable("idUser") Long idUser, @PathVariable("idTicket") Long idTicket, @RequestBody ResponseRequest responseRequest, BindingResult bindingResult) {
        Ticket ticket = ticketService.getTicketById(idTicket);
        User user = userService.getUserByUserId(idUser);
        List<String> errors = new ArrayList<>();
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        responseValidator.validate(responseRequest, bindingResult);
        if (ticket == null) {
            return ResponseEntity.badRequest().body("El ticket con id "+idTicket+" no se encuentra registrado.");
        }
        if (user == null) {
            return ResponseEntity.badRequest().body("El pasante con id "+idUser+" no se encuentra registrado.");
        }
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
            return ResponseEntity.badRequest().body(new ErrorResponse("Error en el formulario.",errors));
        }
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


    @Operation(summary = "Endpoint para responder a una reserva.", description = "Se da respuesta a una reserva según el id del usuario y según el id de la reserva.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se retorna un mensaje que indica que la reserva ha sido atendido exitosamente.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica errores en el formulario, o bien que la reserva o usuario especificados por su id no se encuentran registrados.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/manage/reserve/response/{idUser}/{idReserve}")
    @PreAuthorize("hasRole('PASANTE')")
    public ResponseEntity<?> responseReserve(@PathVariable("idUser") Long idUser, @PathVariable("idReserve") Long idReserve, @RequestBody ResponseRequest responseRequest, BindingResult bindingResult) {
        Reserve reserve = reserveService.getReserveById(idReserve);
        User user = userService.getUserByUserId(idUser);
        List<String> errors = new ArrayList<>();
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        responseValidator.validate(responseRequest, bindingResult);
        if (reserve == null) {
            return ResponseEntity.badRequest().body("El ticket con id "+idReserve+" no se encuentra registrado.");
        }
        if (user == null) {
            return ResponseEntity.badRequest().body("El pasante con id "+idUser+" no se encuentra registrado.");
        }
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
            return ResponseEntity.badRequest().body(new ErrorResponse("Error en el formulario.",errors));
        }
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
