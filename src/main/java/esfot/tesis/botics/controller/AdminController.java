package esfot.tesis.botics.controller;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
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
import esfot.tesis.botics.payload.request.SoftwareRequest;
import esfot.tesis.botics.payload.response.CommentaryResponse;
import esfot.tesis.botics.payload.response.HistoryResponse;
import esfot.tesis.botics.payload.response.ResponseResponse;
import esfot.tesis.botics.service.*;
import esfot.tesis.botics.validator.ComputerValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.*;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/admin")
public class AdminController {
    private final UserServiceImpl userService;

    private final SignupValidator signupValidator;

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final RoleRepository roleRepository;

    private final LabServiceImpl labService;

    private final SoftwareServiceImpl softwareService;

    private final ComputerServiceImpl computerService;

    private final ServletContext servletContext;

    private final HistoryServiceImpl historyService;

    private final TemplateEngine templateEngine;

    private final ComputerValidator computerValidator;

    private final InternController internController;

    private final CommentaryServiceImpl commentaryService;

    @Autowired
    public AdminController(UserServiceImpl userService, SignupValidator signupValidator, UserRepository userRepository, PasswordEncoder encoder, RoleRepository roleRepository, LabServiceImpl labService, SoftwareServiceImpl softwareService, ComputerServiceImpl computerService, ServletContext servletContext, HistoryServiceImpl historyService, TemplateEngine templateEngine, ComputerValidator computerValidator, InternController internController, CommentaryServiceImpl commentaryService) {
        this.userService = userService;
        this.signupValidator = signupValidator;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
        this.labService = labService;
        this.softwareService = softwareService;
        this.computerService = computerService;
        this.servletContext = servletContext;
        this.historyService = historyService;
        this.templateEngine = templateEngine;
        this.computerValidator = computerValidator;
        this.internController = internController;
        this.commentaryService = commentaryService;
    }

    @GetMapping("/interns")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexInterns() {
        List<User> interns = userService.getAllInternUsers();
        if (interns.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen registros."));
        } else {
            return ResponseEntity.ok().body(interns);
        }
    }

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

    @PostMapping("/intern/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveIntern(@RequestBody SignupRequest signUpRequest, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        signupValidator.validate(signUpRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
            return ResponseEntity.badRequest().body(new ErrorResponse("Error en el formulario.",errors));
        }

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Ya existe dicho nombre de usuario."));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Ya existe una cuenta con dicho email."));
        }

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()), 0);

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role -> {
            if ("pasante".equals(role)) {
                Role userRole = roleRepository.findByName(ERole.ROLE_PASANTE).
                        orElseThrow(() -> new RuntimeException("Error: Role not found."));
                roles.add(userRole);
            }
        });

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Pasante registrado."));
    }
    
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

    @GetMapping("/labs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexLabs() {
        List<Lab> labs = labService.getAll();
        if (labs.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen registros."));
        } else {
            return ResponseEntity.ok().body(labs);
        }
    }

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

    @PostMapping("/software/save/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveSoftware(@PathVariable("id") Long id, @RequestBody SoftwareRequest softwareRequest) {
        Lab currentLab = labService.getLabById(id);
        Software software = new Software(softwareRequest.getName(), softwareRequest.getVersion(), softwareRequest.getYear());
        if (softwareService.getSoftwareByName(softwareRequest.getName()) != null) {
            software = softwareService.getSoftwareByName(softwareRequest.getName());
            software.setVersion(softwareRequest.getVersion());
            software.setYear(softwareRequest.getYear());
            softwareService.saveSoftware(software);
        } else {
            currentLab.getSoftwares().add(software);
            labService.saveLab(currentLab);
        }
        return ResponseEntity.ok().body(new MessageResponse("Software list updated."));
    }

    @DeleteMapping("/software/delete/{idLab}/{idSoft}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSoftware(@PathVariable("idLab") Long idLab, @PathVariable("idSoft") Long idSoft) {
        Lab currentLab = labService.getLabById(idLab);
        Software software = softwareService.getSoftware(idSoft);
        currentLab.getSoftwares().remove(software);
        labService.saveLab(currentLab);
        return ResponseEntity.ok().body(new MessageResponse("Software deleted."));
    }

    @PostMapping("/computer/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveComputer(@RequestBody ComputerRequest computerRequest, BindingResult bindingResult) {
        if (computerService.getComputerByHostName(computerRequest.getHostName()) != null || computerService.getComputerBySerialCpu(computerRequest.getSerialCpu()) != null || computerService.getComputerBySerialMonitor(computerRequest.getSerialMonitor()) != null) {
            return ResponseEntity.ok().body(new MessageResponse("El hostname, serial del Cpu o serial del monitor ya se encuentran registrados."));
        }
        List<String> errors = new ArrayList<>();
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages");
        computerValidator.validate(computerRequest, bindingResult);
        /*
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> errors.add(resourceBundleMessageSource.getMessage(e, Locale.US)));
            return ResponseEntity.badRequest().body(new ErrorResponse("Error en el formulario.",errors));
        }
         */
        Computer computer = new Computer(computerRequest.getHostName(), computerRequest.getSerialMonitor(),
                computerRequest.getSerialKeyboard(), computerRequest.getSerialCpu(), computerRequest.getCodeCpu(),
                computerRequest.getCodeMonitor(), computerRequest.getCodeKeyboard(), computerRequest.getState(),
                computerRequest.getModel(), computerRequest.getHardDrive(), computerRequest.getRam(),
                computerRequest.getProcessor(), computerRequest.getOperativeSystem(), computerRequest.getDetails(),
                computerRequest.getObservations(), computerRequest.getLabReference());
        if (computerService.getComputerByHostName(computerRequest.getHostName()) != null) {
            computer = computerService.getComputerByHostName(computerRequest.getHostName());
            computer.setSerialMonitor(computerRequest.getSerialMonitor());
            computer.setSerialKeyboard(computerRequest.getSerialKeyboard());
            computer.setCodeCpu(computerRequest.getCodeCpu());
            computer.setCodeMonitor(computerRequest.getCodeMonitor());
            computer.setCodeKeyboard(computerRequest.getCodeKeyboard());
            computer.setState(computerRequest.getState());
            computer.setModel(computerRequest.getModel());
            computer.setHardDrive(computerRequest.getHardDrive());
            computer.setRam(computerRequest.getRam());
            computer.setProcessor(computerRequest.getProcessor());
            computer.setOperativeSystem(computerRequest.getOperativeSystem());
            computer.setDetails(computerRequest.getDetails());
            computer.setObservations(computerRequest.getObservations());
            computer.setLabReference(computerRequest.getLabReference());
        }
        computerService.saveComputer(computer);
        return ResponseEntity.ok().body(new MessageResponse("Computer saved."));
    }

    @GetMapping("/computers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexComputers() {
        return ResponseEntity.ok().body(computerService.getAll());
    }

    /*
    @GetMapping("/computer/get/{idComputer}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> showComputerById(@PathVariable("idComputer") Long idComputer) {
        Computer computer = computerService.getComputerByID(idComputer);
        if (computer == null) {
            return ResponseEntity.ok().body(new MessageResponse("Computadora no encontrada."));
        } else {
            return ResponseEntity.ok().body(computer);
        }
    }
    */

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

    @DeleteMapping("/computer/delete/{hostName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteComputer(@PathVariable("hostName") String hostName) {
        computerService.deleteComputer(computerService.getComputerByHostName(hostName));
        return ResponseEntity.ok().body(new MessageResponse("Computer deleted."));
    }

    @PutMapping("/computer/assign/{idLab}/{idComputer}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignComputerToLab(@PathVariable("idLab") Long idLab, @PathVariable("idComputer") Long idComputer) {
        Lab currentLab = labService.getLabById(idLab);
        Computer computer = computerService.getComputerByID(idComputer);
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
            history = new History(true, idLab, idComputer);
            historyService.saveHistory(history);
            return ResponseEntity.ok().body(new MessageResponse("Computadora "+computer.getHostName()+" asignada al laboratorio "+currentLab.getName()+"."));
        }
    }

    @PutMapping("/computer/unassign/{idLab}/{idComputer}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unassignComputerFromLab(@PathVariable("idLab") Long idLab, @PathVariable("idComputer") Long idComputer, @RequestParam(name = "changeDetails") String changeDetails) {
        Lab currentLab = labService.getLabById(idLab);
        Computer computer = computerService.getComputerByID(idComputer);
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

    @PutMapping("/computer/reassign/{idLab1}/{idLab2}/{idComputer}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reassignComputerFromLabToLab(@PathVariable("idLab1") Long idLab1, @PathVariable("idLab2") Long idLab2, @PathVariable("idComputer") Long idComputer, @RequestParam(value = "changeDetails") String changeDetails) {
        Lab lab1 = labService.getLabById(idLab1);
        Lab lab2 = labService.getLabById(idLab2);
        Computer computer = computerService.getComputerByID(idComputer);
        this.unassignComputerFromLab(idLab1, idComputer, changeDetails);
        this.assignComputerToLab(idLab2, idComputer);
        return ResponseEntity.ok().body(new MessageResponse("Computadora "+computer.getHostName()+" reasignada del laboratorio "+lab1.getName()+" al laboratorio "+lab2.getName()+"."));
    }

    @GetMapping("/computers/{idLab}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexComputerByIdLab(@PathVariable("idLab") Long idLab) {
        return ResponseEntity.ok().body(computerService.getAllByLabReference(idLab));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexHistory() {
        List<History> histories = historyService.getAllHistories();
        if (histories.isEmpty()) {
            return ResponseEntity.ok().body(new MessageResponse("No existen registros."));
        } else {
            List<HistoryResponse> historyResponses = new ArrayList<>();
            histories.forEach(history -> {
                HistoryResponse historyResponse = new HistoryResponse();
                Lab lab = labService.getLabById(history.getLabReference());
                Computer computer = computerService.getComputerByID(history.getComputerReference());
                historyResponse.setId(history.getId());
                historyResponse.setLabName(lab.getName().toString());
                historyResponse.setHostName(computer.getHostName());
                historyResponse.setCodeCpu(computer.getCodeCpu());
                historyResponse.setChangeDetails(history.getChangeDetails());
                historyResponse.setState(history.isState());
                historyResponses.add(historyResponse);
            });
            return ResponseEntity.ok().body(historyResponses);
        }
    }

    @GetMapping("/inventory/report/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generatePdfReport(HttpServletRequest request, HttpServletResponse response) {
        WebContext context = new WebContext(request, response, servletContext);
        context.setVariable("labs", labService.getAll());
        String ireport = templateEngine.process("ireport", context);
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("https://botics.loca.lt");
        HtmlConverter.convertToPdf(ireport, target, converterProperties);
        byte[] bytes = target.toByteArray();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(bytes);
    }

    @GetMapping("/commentaries")
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

    @GetMapping("/reserves")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexReserves() {
        return internController.indexReserves();
    }

    @GetMapping("/tickets")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexTickets() {
        return internController.indexTickets();
    }

}