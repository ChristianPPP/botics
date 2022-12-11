package esfot.tesis.botics.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseResponse {
    private long id;
    private String subject;
    private String details;
    //Usuario
    private String firstName;
    private String lastName;
    private String email;
    private String role;
}
