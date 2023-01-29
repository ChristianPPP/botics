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
public class AdminTest {
    @Autowired
    private MockMvc mockMvc;

    private final String uri = "/api/v1/admin/";


    @Test
    public void indexInternsShouldReturnOk() throws Exception {
        this.mockMvc.perform(post(uri + "intern/index").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void showInternByNameShouldReturnOk() throws Exception {
        this.mockMvc.perform(get(uri + "intern/" + "Chris")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlzcyI6ImJvdGljcyIsImlhdCI6MTY3MzQ5NzQ0NywiZXhwIjoxNjczNDk4MzQ3fQ.e7nhLiDicFlDLOHyv_W-1EJ3xSJPIgRiixpe2NWw8kKL0QrI717eW7Jyg9lY4cbd9R3mBxhz5HfJeT-U1zOCPg"))
                .andExpect(status().isOk());
    }

    @Test
    public void enableInternShouldReturnOk() throws Exception {
        this.mockMvc.perform(get(uri + "intern/enable/" + 3)
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlzcyI6ImJvdGljcyIsImlhdCI6MTY3MzQ4ODg2MCwiZXhwIjoxNjczNDg5NzYwfQ.nlLR_gEKUNKnOfFcByKnpc4EFu0TldAgGr5UESxig5Za_r4V0KYgV9TrGl36fbi8BW3AlS7ZEngkbQ0Ufj8vXg"))
                .andExpect(status().isOk());
    }

    @Test
    public void indexHistoryShouldReturnOk() throws Exception {
        this.mockMvc.perform(get(uri + "inventory/history/index")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlzcyI6ImJvdGljcyIsImlhdCI6MTY3MzQ5MTMzMSwiZXhwIjoxNjczNDkyMjMxfQ.zigjft4ark4DUJcIcB-tMqLhgfinciNO2uAdU9-JgDZWnkrZ_3pAUWMd99jmPekLO0hhGn4i3D-XpewAGf33AA"))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteComputerShouldReturnOk() throws Exception {
        this.mockMvc.perform(delete(uri + "computer/delete/" + "1-21-01-E022-01")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlzcyI6ImJvdGljcyIsImlhdCI6MTY3MzQ4OTk1MSwiZXhwIjoxNjczNDkwODUxfQ.bAyAh0TJZ-ElBmvAVqr8VyG9u4ox9fz0jG9jS1dWRmnW1kt0_o8YkDZMDryk0atSE6Qc07PuDTxQc7SBH9-f-w"))
                .andExpect(status().isOk());
    }

    @Test
    public void saveComputerShouldReturnOk() throws Exception {
        String request = "{" +
                "\"hostName\": \"1-21-01-E022-03\"," +
                "\"serialMonitor\": \"AU2427\"," +
                "\"serialKeyboard\": \"FXLM78J\"," +
                "\"serialCpu\": \"FXLM78JO\"," +
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
        this.mockMvc.perform(post(uri + "computer/save").contentType(MediaType.APPLICATION_JSON).content(request).header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlzcyI6ImJvdGljcyIsImlhdCI6MTY3MzQ5NjQxOCwiZXhwIjoxNjczNDk3MzE4fQ.KCXKAFR6zBGBB0-I-sSeB-n0bwsGWPTaNUEB4n4ABhNlCqDLBaTDUQgm8m--1JlfXnF_tJikFSvrMZEIgcxPlg")).andExpect(status().isOk());
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

}


