package esfot.tesis.botics.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Indexed;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Indexed
@Table(name = "computers",
        uniqueConstraints = {@UniqueConstraint(columnNames = "host_name"),
                @UniqueConstraint(columnNames = "serial_cpu"),
                @UniqueConstraint(columnNames = "serial_monitor")})
public class Computer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "host_name", nullable = false, length = 30)
    private String hostName;

    @Column(name = "serial_monitor")
    private String serialMonitor;

    @Column(name = "serial_keyboard")
    private String serialKeyboard;

    @Column(name = "serial_cpu")
    private String serialCpu;

    @Column(name = "code_monitor")
    private String codeMonitor;

    @Column(name = "code_keyboard")
    private String codeKeyboard;

    @Column(name = "code_cpu")
    private String codeCpu;

    @Column(name = "state")
    private boolean state;

    @Size(max = 30)
    @Column(name = "model", nullable = false, length = 30)
    private String model;

    @Column(name = "hard_drive")
    private String hardDrive;

    @Column(name = "RAM")
    private String ram;

    @Column(name = "processor")
    private String processor;

    @Column(name = "operative_system")
    private String operativeSystem;

    @Column(name = "details")
    private String details;

    @Column(name = "observations")
    private String observations;

    @Column(name = "lab_reference", nullable = false, columnDefinition = "int default 0")
    private Long labReference;

    //Timestamps
    @Column(name = "created_at")
    @CreationTimestamp
    private Date created_at;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updated_at;

    public Computer(String hostName, String serialMonitor, String serialKeyboard, String serialCpu, String codeCpu,
                    String codeMonitor, String codeKeyboard,
                    boolean state, String model, String hardDrive, String ram, String processor, String operativeSystem,
                    String details, String observations, Long labReference) {
        this.hostName = hostName;
        this.serialMonitor = serialMonitor;
        this.serialKeyboard = serialKeyboard;
        this.serialCpu = serialCpu;
        this.codeCpu = codeCpu;
        this.codeMonitor = codeMonitor;
        this.codeKeyboard = codeKeyboard;
        this.state = state;
        this.model = model;
        this.hardDrive = hardDrive;
        this.ram = ram;
        this.processor = processor;
        this.operativeSystem = operativeSystem;
        this.details = details;
        this.observations = observations;
        this.labReference = labReference;
    }
}
