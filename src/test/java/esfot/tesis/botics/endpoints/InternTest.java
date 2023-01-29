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
public class InternTest {
    @Autowired
    private MockMvc mockMvc;

    private final String uri = "/api/v1/intern/";

    @Test
    public void manageTicketsShouldReturnOk() throws Exception {
        this.mockMvc.perform(get(uri + "manage/ticket/index")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwYXNhbnRlIiwiaXNzIjoiYm90aWNzIiwiaWF0IjoxNjczNDk1MzM1LCJleHAiOjE2NzM0OTYyMzV9.rIF2fgvuvuDpJHLjtkPUtBUPd5aP06PKJ8m2KDy5ZBlD6lgGKIdcza0OBLDbWO4K18au37SJZ1JZV1quLAviag"))
                .andExpect(status().isOk());
    }

    @Test
    public void manageReservesShouldReturnOk() throws Exception {
        this.mockMvc.perform(get(uri + "manage/reserve/index")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwYXNhbnRlIiwiaXNzIjoiYm90aWNzIiwiaWF0IjoxNjczNDk1ODQzLCJleHAiOjE2NzM0OTY3NDN9.ROxW9Q7McfFZtx38u7xrcQHvtdlqKw-peZDlm9nja0KOvKYCYvqPFGi5795wo96ADFwqKXuIkMYTCuXPjEshXg"))
                .andExpect(status().isOk());
    }
}
