package esfot.tesis.botics.payload.response;

import esfot.tesis.botics.entity.Computer;
import esfot.tesis.botics.entity.Lab;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LabResponse {
    private Lab lab;
    private List<Computer> computers;
}
