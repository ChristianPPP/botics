package esfot.tesis.botics.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class TicketResponse {
    private long id;
    private String subject;
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
