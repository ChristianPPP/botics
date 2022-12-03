package esfot.tesis.botics.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReserveRequest {
    private String labName;
    private String description;
}
