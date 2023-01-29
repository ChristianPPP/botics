package esfot.tesis.botics.endpoints;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdministrativeTest {
    @Autowired
    private MockMvc mockMvc;

    private final String uri = "/api/v1/administrative/";

    @Test
    public void indexTicketsShouldReturnOk() throws Exception {
        this.mockMvc.perform(get(uri + "ticket/index/" + 10)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbmlzdHJhdGl2byIsImlzcyI6ImJvdGljcyIsImlhdCI6MTY3MzQ5MzE1MywiZXhwIjoxNjczNDk0MDUzfQ.Hr6zE1ow-p5X-8HKnUcRF0xKh-vMSx1f1JoIlIxOMMDgYqLkyAr3TwF5dpHFUHzGD498abo6w5DjRnWbOEEA2A"))
                .andExpect(status().isOk());
    }

    @Test
    public void manageCommentaryShouldReturnOk() throws Exception {
        this.mockMvc.perform(get(uri + "manage/commentary/index/" + 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbmlzdHJhdGl2byIsImlzcyI6ImJvdGljcyIsImlhdCI6MTY3MzQ5MzcwNCwiZXhwIjoxNjczNDk0NjA0fQ.625KOGBFV9nwWJ8uALbIXFFnR6c_JGL3rJVKE8OLlP1bnU-et_ZL6AzP9YsaiQ9Ygd96Pa525PB2GylagrDFvw"))
                .andExpect(status().isOk());
    }
}
