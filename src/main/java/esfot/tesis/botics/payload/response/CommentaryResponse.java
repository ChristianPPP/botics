package esfot.tesis.botics.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentaryResponse {
    private long id;
    private String subject;
    private String message;
    private boolean state;
    //Usuario
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    //Respuesta
    private ResponseResponse response;
}
