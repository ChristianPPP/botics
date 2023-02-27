package esfot.tesis.botics.controller;

import esfot.tesis.botics.auth.entity.Role;
import esfot.tesis.botics.auth.entity.User;
import esfot.tesis.botics.auth.entity.enums.ERole;
import esfot.tesis.botics.auth.payload.request.SignupRequest;
import esfot.tesis.botics.auth.payload.response.ErrorResponse;
import esfot.tesis.botics.auth.payload.response.MessageResponse;
import esfot.tesis.botics.auth.repository.RoleRepository;
import esfot.tesis.botics.auth.repository.UserRepository;
import esfot.tesis.botics.auth.validator.SignupValidator;
import esfot.tesis.botics.entity.*;
import esfot.tesis.botics.entity.enums.ELab;
import esfot.tesis.botics.payload.request.ComputerRequest;
import esfot.tesis.botics.payload.request.ProfileRequest;
import esfot.tesis.botics.payload.response.CommentaryResponse;
import esfot.tesis.botics.payload.response.HistoryResponse;
import esfot.tesis.botics.payload.response.LabResponse;
import esfot.tesis.botics.payload.response.ResponseResponse;
import esfot.tesis.botics.service.*;
import esfot.tesis.botics.validator.ComputerValidator;
import esfot.tesis.botics.validator.ProfileValidator;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@ApiResponses(value= {
        @ApiResponse(responseCode = "401", description = "Esta respuesta indica un fallo de autenticación, para los endpoints privados esto puede inidicar que el JWT token ha caducado y se requiere obtener un nuevo token.", content =
                {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
})
@RequestMapping("api/v1/admin")
public class AdminController {
    private final UserServiceImpl userService;

    private final SignupValidator signupValidator;

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final RoleRepository roleRepository;

    private final LabServiceImpl labService;

    private final ComputerServiceImpl computerService;

    private final HistoryServiceImpl historyService;

    private final ComputerValidator computerValidator;

    private final InternController internController;

    private final CommentaryServiceImpl commentaryService;

    private final ProfileValidator profileValidator;

    private final AvatarServiceImpl avatarService;

    @Autowired
    public AdminController(UserServiceImpl userService, SignupValidator signupValidator, UserRepository userRepository, PasswordEncoder encoder, RoleRepository roleRepository, LabServiceImpl labService, ComputerServiceImpl computerService, HistoryServiceImpl historyService, ComputerValidator computerValidator, InternController internController, CommentaryServiceImpl commentaryService, ProfileValidator profileValidator, AvatarServiceImpl avatarService) {
        this.userService = userService;
        this.signupValidator = signupValidator;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
        this.labService = labService;
        this.computerService = computerService;
        this.historyService = historyService;
        this.computerValidator = computerValidator;
        this.internController = internController;
        this.commentaryService = commentaryService;
        this.profileValidator = profileValidator;
        this.avatarService = avatarService;
    }



    @Operation(summary = "Endpoint para listar los usuarios con rol 'pasante'.", description = "Retorna un arreglo de perfiles de usuario con el rol pasante.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá la lista de todos los perfiles de usuario con rol pasante o un mensaje si no existen registros.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/intern/index")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexInterns() {
        List<User> interns = userService.getAllInternUsers();
        if (interns.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen registros."));
        } else {
            return ResponseEntity.ok().body(interns);
        }
    }

    @Operation(summary = "Endpoint para obtener un pasante por su nombre.", description = "Retorna un pasante según su nombre en el caso en que el usuario al que se hace referencia haya actualizado su perfil de usuario.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá el pasante con el nombre especificado o un mensaje si no se ha encontrado el usuario con dicho nombre.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/intern/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> showIntern(@PathVariable("name") String name) {
        User intern = userService.getInternUser(name);
        if (intern == null) {
            return ResponseEntity.ok().body(new MessageResponse("El pasante con nombre "+name+" no se encuentra registrado."));
        } else {
            return ResponseEntity.ok().body(intern);
        }
    }

    @Operation(summary = "Endpoint para crear una nueva cuenta de usuario con el rol 'pasante'", description = "Se registran los datos de una cuenta de usuario de la misma forma que en el registro general con la diferencia de que únicamente es admitido el rol 'pasante'.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá un mensaje de registro exitoso.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica errores en el formulario o bien que el nombre de usuario o email ya se encuentran registrados.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/intern/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveIntern(@RequestBody SignupRequest signUpRequest, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        signupValidator.validate(signUpRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
            return ResponseEntity.badRequest().body(new ErrorResponse("Errores en el formulario.",errors));
        }

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Nombre de usuario ya registrado."));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email ya registrado."));
        }

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()), 0);

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (!signUpRequest.getRole().contains("pasante")) {
            return ResponseEntity.badRequest().body(new MessageResponse("Rol especificado no válido."));
        }
        strRoles.forEach(role -> {
            if ("pasante".equals(role)) {
                Role userRole = roleRepository.findByName(ERole.ROLE_PASANTE).
                        orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                roles.add(userRole);
            }
        });
        user.setRoles(roles);
        ProfileRequest profileRequest = new ProfileRequest(signUpRequest.getFirstName(), signUpRequest.getLastName(), 0);
        if (!Objects.equals(signUpRequest.getFirstName(), "") && !Objects.equals(signUpRequest.getLastName(), "")) {
            profileValidator.validate(profileRequest, bindingResult);
            if (bindingResult.hasErrors()) {
                bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
                return ResponseEntity.badRequest().body(new ErrorResponse("Errores en el formulario.",errors));
            } else {
                user.setFirstName(signUpRequest.getFirstName());
                user.setLastName(signUpRequest.getLastName());
                user.setExtension(0);
                Avatar newAvatar = new Avatar("default", "image/jpg", "https://res.cloudinary.com/botics/image/upload/v1675551261/default-profile-icon-24_oi9wti.jpg");
                avatarService.save(newAvatar);
                user.setAvatar(newAvatar);
                userRepository.save(user);
                return ResponseEntity.ok(new MessageResponse("Pasante registrado."));
            }
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Nombre o apellido no válidos."));
        }
    }


    @Operation(summary = "Endpoint para inhabilitar una cuenta de usuario con rol 'pasante'.", description = "Inhabilita la cuenta de usuario con rol pasante mediante el id especificado.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá un mensaje que indica que la cuenta con el id especificado ha sido inhabilitada.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica que la cuenta de usuario con rol pasante con el id especificado no se encuentra registrada.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/intern/disable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> disableIntern(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("El pasante no se encuentra registrado."));
        } else {
            user.setState(false);
            userRepository.save(user);
            return ResponseEntity.ok().body(new MessageResponse("Pasante inhabilitado."));
        }
    }

    @Operation(summary = "Endpoint para habilitar una cuenta de usuario con rol 'pasante'.", description = "Habilita la cuenta de usuario con rol pasante mediante el id especificado.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá un mensaje que indica que la cuenta con el id especificado ha sido habilitada.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica que la cuenta de usuario con rol pasante con el id especificado no se encuentra registrada.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping("/intern/enable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> enableIntern(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("El pasante no se encuentra registrado."));
        } else {
            user.setState(true);
            userRepository.save(user);
            return ResponseEntity.ok().body(new MessageResponse("Pasante habilitado."));
        }
    }

    @Operation(summary = "Endpoint para listar los laboratorios registrados.", description = "Retorna un arreglo de laboratorios.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá la lista de los laboratorios registrados o un mensaje si no existen registros.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/lab/index")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexLabs() {
        List<Lab> labs = labService.getAll();
        if (labs.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen registros."));
        } else {
            return ResponseEntity.ok().body(labs);
        }
    }

    @Operation(summary = "Endpoint para obtener un laboratorio por su nombre.", description = "Retorna un laboratorio según su nombre, estos pueden ser: 'smd', '14', '15', '22A', '22B'.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá el laboratorio con el nombre especificado o un mensaje si no se ha encontrado el laboratorio con dicho nombre.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/lab/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> showLab(@PathVariable("name") String name) {
        if ("smd".equals(name)) {
            return ResponseEntity.ok().body(labService.getLabByName(ELab.LAB_SMD));
        }
        if ("14".equals(name)) {
            return ResponseEntity.ok().body(labService.getLabByName(ELab.LAB_ET14));
        }
        if ("15".equals(name)) {
            return ResponseEntity.ok().body(labService.getLabByName(ELab.LAB_ET15));
        }
        if ("16".equals(name)) {
            return ResponseEntity.ok().body(labService.getLabByName(ELab.LAB_ET16));
        }
        if ("22A".equals(name)) {
            return ResponseEntity.ok().body(labService.getLabByName(ELab.LAB_ET22A));
        }
        if ("22B".equals(name)) {
            return ResponseEntity.ok().body(labService.getLabByName(ELab.LAB_ET22B));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Laboratorio no encontrado."));
    }


    @Operation(summary = "Endpoint para crear una nueva computadora", description = "Se registran los datos de una computadora, siendo que los campos 'hostname', 'serialCpu' y 'serialMonitor' deben ser únicos.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá un mensaje que indica que el computador ha sido registrado o bien que el nombre de host, serial del Cpu o serial del monitor ya se encuentran registrados..", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica errores en el formulario.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/computer/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveComputer(@RequestBody ComputerRequest computerRequest, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        computerValidator.validate(computerRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
            return ResponseEntity.badRequest().body(new ErrorResponse("Errores en el formulario.",errors));
        }
        if (computerService.getComputerByHostName(computerRequest.getHostName()) != null || computerService.getComputerBySerialCpu(computerRequest.getSerialCpu()) != null || computerService.getComputerBySerialMonitor(computerRequest.getSerialMonitor()) != null) {
            return ResponseEntity.ok().body(new MessageResponse("El hostname, serial del Cpu o serial del monitor ya se encuentran registrados."));
        }
        Computer computer = new Computer(computerRequest.getHostName(), computerRequest.getSerialMonitor(),
                computerRequest.getSerialKeyboard(), computerRequest.getSerialCpu(), computerRequest.getCodeCpu(),
                computerRequest.getCodeMonitor(), computerRequest.getCodeKeyboard(), true,
                computerRequest.getModel(), computerRequest.getHardDrive(), computerRequest.getRam(),
                computerRequest.getProcessor(), computerRequest.getOperativeSystem(), computerRequest.getDetails(),
                computerRequest.getObservations(), computerRequest.getLabReference());
        computerService.saveComputer(computer);
        return ResponseEntity.ok().body(new MessageResponse("Computador registrado."));
    }

    @Operation(summary = "Endpoint para actualizar los datos de una computadora", description = "Se actualizaran los datos de una computadora, siendo que los campos 'hostname', 'serialCpu' y 'serialMonitor' no serán modificados.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá un mensaje que indica que el computador ha sido registrado o bien que el nombre de host, serial del Cpu o serial del monitor ya se encuentran registrados..", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica errores en el formulario o que la computadora especificada no se encuentra registrada.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PutMapping("/computer/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateComputer(@RequestBody ComputerRequest computerRequest, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        computerValidator.validate(computerRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
            return ResponseEntity.badRequest().body(new ErrorResponse("Errores en el formulario.",errors));
        }
        Computer computer = computerService.getComputerByHostName(computerRequest.getHostName());
        if (computer != null) {
            computer = computerService.getComputerByHostName(computerRequest.getHostName());
            computer.setSerialMonitor(computerRequest.getSerialMonitor());
            computer.setSerialKeyboard(computerRequest.getSerialKeyboard());
            computer.setCodeCpu(computerRequest.getCodeCpu());
            computer.setCodeMonitor(computerRequest.getCodeMonitor());
            computer.setCodeKeyboard(computerRequest.getCodeKeyboard());
            computer.setState(true);
            computer.setModel(computerRequest.getModel());
            computer.setHardDrive(computerRequest.getHardDrive());
            computer.setRam(computerRequest.getRam());
            computer.setProcessor(computerRequest.getProcessor());
            computer.setOperativeSystem(computerRequest.getOperativeSystem());
            computer.setDetails(computerRequest.getDetails());
            computer.setObservations(computerRequest.getObservations());
            computer.setLabReference(computerRequest.getLabReference());
            computerService.saveComputer(computer);
            return ResponseEntity.ok().body(new MessageResponse("Datos del computador actualizados."));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("La computadora especificada no se encuentra registrada."));
        }
    }


    @Operation(summary = "Endpoint para habilitar una computadora'.", description = "Habilita la computadora mediante el id especificado.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá un mensaje que indica que la computadora con el id especificado ha sido habilitada.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica que la computadora con el id especificado no se encuentra registrada.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PutMapping("/computer/enable/{idComputer}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> enableComputer(@PathVariable("idComputer") Long idComputer) {
        Computer computer = computerService.getComputerByID(idComputer);
        if (computer == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("La computadora con id "+idComputer+" no se encuentra registrada."));
        } else {
            computer.setState(true);
            computerService.saveComputer(computer);
            return ResponseEntity.ok().body(new MessageResponse("La computadora "+computer.getHostName()+" se encuentra en estado activo."));
        }
    }


    @Operation(summary = "Endpoint para inhabilitar una computadora'.", description = "Inhabilita la computadora mediante el id especificado.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá un mensaje que indica que la computadora con el id especificado ha sido inhabilitada.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Esta respuesta indica que la computadora con el id especificado no se encuentra registrada.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PutMapping("/computer/disable/{idComputer}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> disableComputer(@PathVariable("idComputer") Long idComputer) {
        Computer computer = computerService.getComputerByID(idComputer);
        if (computer == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("La computadora con id "+idComputer+" no se encuentra registrada."));
        } else {
            computer.setState(false);
            computerService.saveComputer(computer);
            return ResponseEntity.ok().body(new MessageResponse("La computadora "+computer.getHostName()+" se encuentra en estado inactivo."));
        }
    }


    @Operation(summary = "Endpoint para listar las computadoras registradas.", description = "Retorna un arreglo de computadoras.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá la lista de las computadoras registradas o un mensaje si no existen registros.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/computer/index")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexComputers() {
        List<Computer> computers = computerService.getAll();
        if (computers.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("No existen computadoras registradas."));
        } else {
            return ResponseEntity.ok().body(computerService.getAll());
        }
    }


    @Operation(summary = "Endpoint para obtener una computadora por su nombre de host.", description = "Retorna una computadora según su nombre de host.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá la computadora con el nombre de host especificado o un mensaje si no se ha encontrado la computadora con dicho nombre de host.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/computer/{hostName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> showComputerByHostName(@PathVariable("hostName") String hostName) {
        Computer computer = computerService.getComputerByHostName(hostName);
        if (computer == null) {
            return ResponseEntity.ok().body(new MessageResponse("Computadora no encontrada."));
        } else {
            return ResponseEntity.ok().body(computerService.getComputerByHostName(hostName));
        }
    }

    @Operation(summary = "Endpoint para asignar una computadora a un laboratorio.", description = "Asigna una computadora según su id a un laboratorio según el id de este y adicionalmente se almacena un registro del historial.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Retorna un mensaje que describe la asginación de la computadora especificada por su id al laboratorio especificado por su id.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Puede indicar que la el laboratorio o la computadora no se encuentran registrados o que la computadora a asignar ya se encuentra asignada a un laboratorio.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @PutMapping("/computer/assign/{idLab}/{idComputer}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignComputerToLab(@PathVariable("idLab") Long idLab, @PathVariable("idComputer") Long idComputer) {
        Lab currentLab = labService.getLabById(idLab);
        Computer computer = computerService.getComputerByID(idComputer);
        if (currentLab == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("El laboratorio con el id "+idLab+" no se encuentra registrado."));
        }
        if (computer == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("El computador con el id "+idComputer+" no se encuentra registrado."));
        }
        boolean state = false;
        History history = historyService.getActualAssigment(idComputer);
        Lab lab = null;
        if (!(history == null)) {
            state =true;
            lab = labService.getLabById(history.getLabReference());
        }
        if (state) {
            assert lab != null;
            return ResponseEntity.badRequest().body(new MessageResponse("La computadora "+computer.getHostName()+" ya se encuentra asignada al laboratorio "+lab.getName()+"."));
        }else {
            computer.setLabReference(idLab);
            computerService.saveComputer(computer);
            currentLab.getComputers().add(computer);
            labService.saveLab(currentLab);
            history = new History(true, idLab, idComputer, currentLab.getName().toString(), computer.getHostName(), computer.getCodeCpu());
            historyService.saveHistory(history);
            return ResponseEntity.ok().body(new MessageResponse("Computadora "+computer.getHostName()+" asignada al laboratorio "+currentLab.getName()+"."));
        }
    }


    @Operation(summary = "Endpoint para desasignar una computadora a un laboratorio.", description = "Desasigna una computadora según su id a un laboratorio según el id de este y adicionalmente se almacena un registro del historial para lo cual se requiere registrar los detalles de la desasignación.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Retorna un mensaje que describe la desasginación de la computadora especificada por su id del laboratorio especificado por su id.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Puede indicar que la el laboratorio o la computadora no se encuentran registrados o que la computadora a desasignar no se encuentra asignada a un laboratorio.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @PutMapping("/computer/unassign/{idLab}/{idComputer}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unassignComputerFromLab(@PathVariable("idLab") Long idLab, @PathVariable("idComputer") Long idComputer, @RequestParam(name = "changeDetails") String changeDetails) {
        Lab currentLab = labService.getLabById(idLab);
        Computer computer = computerService.getComputerByID(idComputer);
        if (currentLab == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("El laboratorio con el id "+idLab+" no se encuentra registrado."));
        }
        if (computer == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("El computador con el id "+idComputer+" no se encuentra registrado."));
        }
        boolean state = false;
        History history = historyService.getActualAssigment(idComputer);
        if (!(history == null)) {
            state = true;
        }
        if (!state) {
            return ResponseEntity.badRequest().body(new MessageResponse("La computadora "+computer.getHostName()+" no se encuentra asignada a un laboratorio."));
        }else {
            computer.setLabReference(0L);
            computerService.saveComputer(computer);
            history.setState(false);
            history.setChangeDetails(changeDetails);
            historyService.saveHistory(history);
            return ResponseEntity.ok().body(new MessageResponse("Computadora "+computer.getHostName()+" desasignada del laboratorio "+currentLab.getName()+"."));
        }
    }


    @Operation(summary = "Endpoint para reasignar una computadora a un laboratorio.", description = "Reasigna una computadora según su id de un laboratorio según el id a otro laboratorio según su id y adicionalmente se almacena un registro del historial para lo cual se requiere registrar los detalles de la reasignación.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Retorna un mensaje que describe la reasginación de la computadora especificada por su id del laboratorio especificado por su id al laboratorio especificado por su id.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Puede indicar que el laboratorio de destino, el laboratorio de origen o la computadora no se encuentran registrados", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @PutMapping("/computer/reassign/{idLab1}/{idLab2}/{idComputer}")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<?> reassignComputerFromLabToLab(@PathVariable("idLab1") Long idLab1, @PathVariable("idLab2") Long idLab2, @PathVariable("idComputer") Long idComputer, @RequestParam(value = "changeDetails") String changeDetails) {
        Lab lab1 = labService.getLabById(idLab1);
        Lab lab2 = labService.getLabById(idLab2);
        Computer computer = computerService.getComputerByID(idComputer);
        if (lab1 == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("El laboratorio con el id "+idLab1+" no se encuentra registrado."));
        }
        if (lab2 == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("El laboratorio con el id "+idLab2+" no se encuentra registrado."));
        }
        if (computer == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("El computador con el id "+idComputer+" no se encuentra registrado."));
        }
        this.unassignComputerFromLab(idLab1, idComputer, changeDetails);
        this.assignComputerToLab(idLab2, idComputer);
        return ResponseEntity.ok().body(new MessageResponse("Computadora "+computer.getHostName()+" reasignada del laboratorio "+lab1.getName()+" al laboratorio "+lab2.getName()+"."));
    }


    @Operation(summary = "Endpoint para listar las computadoras registradas según su pertenencia a algún laboratorio.", description = "Retorna un arreglo de computadoras según el laboratorio especificado por su id al cual han sido asignados.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá la lista de las computadoras registradas por laboratorio o un mensaje si no existen computadoras asignadas al laboratorio especificado.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/computer/index/{idLab}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexComputerByIdLab(@PathVariable("idLab") Long idLab) {
        List<Computer> computers = computerService.getAllByLabReference(idLab);
        if (computers.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("El laboratorio "+idLab+" no tiene computadoras asignadas."));
        } else {
            return ResponseEntity.ok().body(computers);
        }
    }


    @Operation(summary = "Endpoint para listar el historial de movimiento de computadoras.", description = "Retorna un arreglo de asignaciones, desasignaciones y cambios de computadoras entre laboratorios para generar el reporte en PDF.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá la lista del historial registrado o un mensaje si no existen registros.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/inventory/history/index")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexHistory() {
        List<History> histories = historyService.getAllHistories();
        if (histories.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen registros."));
        } else {
            List<HistoryResponse> historyResponses = new ArrayList<>();
            histories.forEach(history -> {
                HistoryResponse historyResponse = new HistoryResponse();
                historyResponse.setId(history.getId());
                historyResponse.setLabName(history.getLabName());
                historyResponse.setHostName(history.getHostName());
                historyResponse.setCodeCpu(history.getCodeCpu());
                historyResponse.setChangeDetails(history.getChangeDetails());
                historyResponse.setState(history.isState());
                historyResponse.setCreatedAt(history.getCreatedAt());
                historyResponse.setUpdatedAt(history.getUpdatedAt());
                historyResponses.add(historyResponse);
            });
            return ResponseEntity.ok().body(historyResponses);
        }
    }

    @Operation(summary = "Endpoint para listar laboratorios y computadoras.", description = "Retorna un arreglo de laboratorios con sus respectivos arreglos de computadoras para generar el reporte en PDF.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá la lista de laboratorios con sus computadoras o un mensaje si no existen registros.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = LabResponse.class))})
    })
    @GetMapping("/inventory/labs/computers/index")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexComputersByLabs() {
        List<Lab> labs = labService.getAll();
        List<LabResponse> labResponses = new ArrayList<>();
        if (labs.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen registros."));
        } else {
            labs.forEach((lab) -> {
                LabResponse labResponse = new LabResponse();
                labResponse.setLab(lab);
                labResponse.setComputers(computerService.getAllByLabReference(lab.getId()));
                labResponses.add(labResponse);
            });
            return ResponseEntity.ok().body(labResponses);
        }
    }


    @Operation(summary = "Endpoint para listar los comentarios registrados.", description = "Retorna un arreglo de comentarios con el detalle de que usuario los realizó, cual ha sido su respuesta y que usuario dio dicha respuesta.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá la lista de comentarios y sus respuestas o un mensaje si no existen registros.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/commentary/index")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexCommentaries() {
        List<Commentary> commentaries = commentaryService.getCommentaries();
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
                commentaryResponse.setCreatedAt(commentary.getCreatedAt());
                commentaryResponse.setUpdatedAt(commentary.getUpdatedAt());
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
                    responseResponse.setCreatedAt(commentary.getResponse().getCreatedAt());
                    responseResponse.setUpdatedAt(commentary.getResponse().getUpdatedAt());
                    commentaryResponse.setResponse(responseResponse);
                }
                commentaryResponses.add(commentaryResponse);
            });
            return ResponseEntity.ok().body(commentaryResponses);
        }
    }

    @Operation(summary = "Endpoint para listar las reservas registradas.", description = "Retorna un arreglo de reservas con el detalle de que usuario las realizó, cual ha sido su respuesta y que usuario dio dicha respuesta.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá la lista de reservas y sus respuestas o un mensaje si no existen registros.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/reserve/index")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexReserves() {
        return internController.indexReserves();
    }

    @Operation(summary = "Endpoint para listar los tickets de asistencia registrados.", description = "Retorna un arreglo de los tickets de asistencia con el detalle de que usuario los realizó, cual ha sido su respuesta y que usuario dio dicha respuesta.")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Se devolverá la lista de tickets de asistencia y sus respuestas o un mensaje si no existen registros.", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    })
    @GetMapping("/ticket/index")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexTickets() {
        return internController.indexTickets();
    }
}