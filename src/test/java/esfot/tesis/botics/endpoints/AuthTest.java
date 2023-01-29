package esfot.tesis.botics.endpoints;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthTest {
    @Autowired
    private MockMvc mockMvc;

    private final String uri = "/api/v1/auth/";

    @Test
    public void signinShouldReturnOk() throws Exception {
        String request = "{" +
                "\"username\": \"admin\"," +
                "\"password\": \"Secret1*\"}";
        this.mockMvc.perform(post(uri + "signin").contentType(MediaType.APPLICATION_JSON).content(request)).andExpect(status().isOk());
    }

    @Test
    public void signupShouldReturnOk() throws Exception {
        String request = "{" +
                "\"username\": \"profesor24\"," +
                "\"email\": \"profesor24@epn.edu.ec\"," +
                "\"role\": [\"profesor\"]," +
                "\"password\": \"Secret1*\"" +
                "}";
        this.mockMvc.perform(post(uri + "signup").contentType(MediaType.APPLICATION_JSON).content(request)).andExpect(status().isOk());
    }

    @Test
    public void resetPasswordShouldReturnOk() throws Exception {
        String request = "{" +
                "\"password\": \"Secret2*\"," +
                "\"confirmPassword\":  \"Secret2*\"" +
                "}";
        this.mockMvc.perform(post(uri + "password/reset" + "?token=BWFm55VZw7adTD34538ByHwsL32PHk").contentType(MediaType.APPLICATION_JSON).content(request)).andExpect(status().isOk());
    }

}
