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
import esfot.tesis.botics.entity.Computer;
import esfot.tesis.botics.entity.Lab;
import esfot.tesis.botics.entity.Software;
import esfot.tesis.botics.entity.enums.ELab;
import esfot.tesis.botics.payload.request.ComputerRequest;
import esfot.tesis.botics.payload.request.SoftwareRequest;
import esfot.tesis.botics.service.ComputerServiceImpl;
import esfot.tesis.botics.service.LabServiceImpl;
import esfot.tesis.botics.service.SoftwareServiceImpl;
import esfot.tesis.botics.service.UserServiceImpl;
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

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/admin")
public class AdminController {
    @Autowired
    UserServiceImpl userService;

    @Autowired
    SignupValidator signupValidator;


    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    LabServiceImpl labService;

    @Autowired
    SoftwareServiceImpl softwareService;

    @Autowired
    ComputerServiceImpl computerService;

    @Autowired
    ServletContext servletContext;

    private final TemplateEngine templateEngine;

    public AdminController(TemplateEngine templateEngine){
        this.templateEngine = templateEngine;
    }

    @GetMapping("/interns")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexInterns() {
        return ResponseEntity.ok().body(userService.getAllInternUsers());
    }

    @GetMapping("/intern/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> showIntern(@PathVariable("name") String name) {
        return ResponseEntity.ok().body(userService.getInternUser(name));
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
            return ResponseEntity.badRequest().body(new ErrorResponse("Form error.",errors));
        }

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
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

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    
    @GetMapping("/intern/disable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> disableIntern(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Intern not found."));
        }
        user.setState(false);
        userRepository.save(user);
        return ResponseEntity.ok().body(new MessageResponse("Intern disable successfully."));
    }

    @GetMapping("/intern/enable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> enableIntern(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Intern not found."));
        }
        user.setState(true);
        userRepository.save(user);
        return ResponseEntity.ok().body(new MessageResponse("Intern enable successfully."));
    }

    @GetMapping("/labs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> indexLabs() {
        return ResponseEntity.ok().body(labService.getAll());
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
        return ResponseEntity.badRequest().body(new MessageResponse("Lab not found."));
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
    public ResponseEntity<?> saveComputer(@RequestBody ComputerRequest computerRequest) {
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

    @GetMapping("/computer/{hostName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> showComputerByHostName(@PathVariable("hostName") String hostName) {
        return ResponseEntity.ok().body(computerService.getComputerByHostName(hostName));
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
        computer.setLabReference(idLab);
        computerService.saveComputer(computer);
        computerService.assignComputerToLab(idLab, idComputer);
        return ResponseEntity.ok().body(new MessageResponse("Computer "+computer.getHostName()+" assigned to lab "+currentLab.getName()+"."));
    }

    @PutMapping("/computer/unassign/{idLab}/{idComputer}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unassignComputerFromLab(@PathVariable("idLab") Long idLab, @PathVariable("idComputer") Long idComputer) {
        Lab currentLab = labService.getLabById(idLab);
        Computer computer = computerService.getComputerByID(idComputer);
        computer.setLabReference(0L);
        computer.setLab(null);
        computerService.saveComputer(computer);
        return ResponseEntity.ok().body(new MessageResponse("Computer "+computer.getHostName()+" unassigned from lab "+currentLab.getName()+"."));
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
}