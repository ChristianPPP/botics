package esfot.tesis.botics.auth.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserInfoResponse {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private String JwtToken;

    public UserInfoResponse(Long id, String username, String email, List<String> roles, String JwtToken) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.JwtToken = JwtToken;
    }
}