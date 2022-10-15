package esfot.tesis.botics.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileRequest {
    private String firstName;
    private String lastName;
    private Integer extension;
}
