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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfileTest {
    @Autowired
    private MockMvc mockMvc;

    private final String uri = "/api/v1/profile/";

    @Test
    public void updateProfileInfoShouldReturnOk() throws Exception {
        String request = "{" +
                "\"firstName\": \"Poleth\"," +
                "\"lastName\": \"Arias\"," +
                "\"extension\": " + 4734 +
                "}";
        this.mockMvc.perform(post(uri + "update/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlzcyI6ImJvdGljcyIsImlhdCI6MTY3MzQ4Nzk2OCwiZXhwIjoxNjczNDg4ODY4fQ.HK2R-DtjIhhBg-AAFhoudqL75vATLIv_CpuRPVla7JyE5Wn2qHL-3j3j7HvDrJhPTbhAY-E_bUT8cqDO1gL_mQ"))
                .andExpect(status().isOk());
    }

}
