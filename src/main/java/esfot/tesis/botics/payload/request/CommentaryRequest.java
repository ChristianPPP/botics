package esfot.tesis.botics.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentaryRequest {
    private String subject;
    private String message;
}
