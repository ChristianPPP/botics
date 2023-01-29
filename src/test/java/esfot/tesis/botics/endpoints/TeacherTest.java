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
public class TeacherTest {
    @Autowired
    private MockMvc mockMvc;

    private final String uri = "/api/v1/teacher/";

    @Test
    public void indexCommentaryShouldReturnOk() throws Exception {
        this.mockMvc.perform(get(uri + "commentary/index/" + 15)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwcm9mZXNvciIsImlzcyI6ImJvdGljcyIsImlhdCI6MTY3MzQ5NDU1NywiZXhwIjoxNjczNDk1NDU3fQ.gJlNDqVhYPpppvSQRJ_HLG2cYmMRUVG0z9KVMFyMXc5wuTPugp4kBFiTOfKdNEu_OHQ8qnc8xelCc1iW95vLjQ"))
                .andExpect(status().isOk());

    }
}
