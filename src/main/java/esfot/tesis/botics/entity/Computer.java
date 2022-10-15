package esfot.tesis.botics.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "computers",
        uniqueConstraints = @UniqueConstraint(columnNames = "host_name"))
public class Computer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @NotBlank
    @Size(max = 30)
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

    @ColumnDefault("1")
    @Column(name = "state", nullable = false)
    private String state;

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


    @ManyToOne(fetch = FetchType.LAZY)
    private Lab lab;

    public Computer(String hostName, String serialMonitor, String serialKeyboard, String serialCpu,
                    String state, String model, String hardDrive, String ram, String processor, String operativeSystem,
                    String details, String observations) {
        this.hostName = hostName;
        this.serialMonitor = serialMonitor;
        this.serialKeyboard = serialKeyboard;
        this.serialCpu = serialCpu;
        this.state = state;
        this.model = model;
        this.hardDrive = hardDrive;
        this.ram = ram;
        this.processor = processor;
        this.operativeSystem = operativeSystem;
        this.details = details;
        this.observations = observations;
    }
}
