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
public class AdminTest {
    @Autowired
    private MockMvc mockMvc;

    private final String uri = "/api/v1/admin/";


    @Test
    public void indexInternsShouldReturnOk() throws Exception {
        this.mockMvc.perform(post(uri + "intern/index").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void saveComputerShouldReturnOk() throws Exception {
        String request = "{" +
                "\"hostName\": \"1-21-01-E022-01\"," +
                "\"serialMonitor\": \"AU2425\"," +
                "\"serialKeyboard\": \"FXLM78J\"," +
                "\"serialCpu\": \"FXLM78J\"," +
                "\"codeMonitor\": \"887195045\"," +
                "\"codeKeyboard\": \"887195045\"," +
                "\"codeCpu\": \"887195045\"," +
                "\"model\": \"HP optiplex 9020\"," +
                "\"hardDrive\": \"1TB\"," +
                "\"ram\": \"16GB\"," +
                "\"processor\": \"i 7700\"," +
                "\"operativeSystem\": \"Windows 7\"," +
                "\"details\": \"Computador nuevo.\"," +
                "\"observations\": \"Actualización de drivers.\"," +
                "\"labReference\": "+ 0 +
                "}";
        this.mockMvc.perform(post(uri + "computer/save").contentType(MediaType.APPLICATION_JSON).content(request).header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlzcyI6ImJvdGljcyIsImlhdCI6MTY3Mjk3NzEyMywiZXhwIjoxNjcyOTc4MDIzfQ.8YqzujWl8SZ1G6lwFpvKhCUW2ARTULebGNhfWkFx8lKuztnhqOlH6q2jxCSdjFtZHH2P2sy9ulimsn96U3yNhg")).andExpect(status().isOk());
    }

    @Test
    public void indexComputersShouldReturnOk() throws Exception {
        this.mockMvc.perform(get(uri + "computer/index")
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                        "Authorization",
                        "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlzcyI6ImJvdGljcyIsImlhdCI6MTY3MzAxODE2OSwiZXhwIjoxNjczMDE5MDY5fQ.VJnP7o4NRNRuwgo6mcm9iU-7JFMOaW1ZLJ-1echfuKHrEzdcN_BYxvEzEdDsJymvQyZucbaMM6r5CienqcJcmA"))
                .andExpect(status().isOk());
    }

    @Test
    public void reassignComputerShouldReturnOk() throws Exception {
        this.mockMvc.perform(
                put(uri + "/computer/reassign/"+2+"/"+3+"/"+130)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlzcyI6ImJvdGljcyIsImlhdCI6MTY3MzAyMTg2NiwiZXhwIjoxNjczMDIyNzY2fQ.MEQ9mH7N6VlhDN8iI0yDKFrBmpumdKVw5DmAElDse9TikpWZWCTLMpcF3viZWalzdn-91_4TFfo4fKmFKrvR8A"))
                .andExpect(status().is(400));
    }

    @Test
    public void indexHistoryShouldReturnOk() throws Exception {
        this.mockMvc.perform(
                get(uri + "inventory/history/index")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization","Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlzcyI6ImJvdGljcyIsImlhdCI6MTY3MzAyMDY5MywiZXhwIjoxNjczMDIxNTkzfQ.ax1LhBl88ENmgCqUR9GB92n_1atlZcNs90HsNwq0TAOIA8AnNkwJpgyDcDBu1w38KAhiWI9nQl0wKYckpdX0uA")
        ).andExpect(status().isOk());
    }
}


