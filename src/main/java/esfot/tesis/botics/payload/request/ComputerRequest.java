package esfot.tesis.botics.payload.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComputerRequest {
    private String hostName;

    private String serialMonitor;

    private String serialKeyboard;

    private String serialCpu;

    private String codeMonitor;

    private String codeKeyboard;

    private String codeCpu;

    private String state;

    private String model;

    private String hardDrive;

    private String ram;

    private String processor;

    private String operativeSystem;

    private String details;

    private String observations;

    private Long labReference;
}
