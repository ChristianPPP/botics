package esfot.tesis.botics.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ReserveResponse {
    private long id;
    private String labName;
    private String description;
    private boolean state;
    private Date createdAt;
    private Date updatedAt;
    //Usuario
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    //Respuesta
    private ResponseResponse response;
}
