package esfot.tesis.botics.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoftwareRequest {
    private String name;
    private String version;
    private int year;
}
