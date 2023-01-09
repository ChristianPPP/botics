package esfot.tesis.botics.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ResponseResponse {
    private long id;
    private String subject;
    private String details;
    private Date createdAt;
    private Date updatedAt;
    //Usuario
    private String firstName;
    private String lastName;
    private String email;
    private String role;
}
