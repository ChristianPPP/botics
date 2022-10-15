package esfot.tesis.botics.auth.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private String password;
    private String confirmPassword;
}
