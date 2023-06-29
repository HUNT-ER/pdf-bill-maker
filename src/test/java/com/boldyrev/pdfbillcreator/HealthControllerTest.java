package com.boldyrev.pdfbillcreator;

import com.boldyrev.pdfbillcreator.controllers.HealthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(HealthController.class)
@WithMockUser
public class HealthControllerTest {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private MockMvc mockMvc;

    @Test
    void checkAvailability_ReturnsHttpStatus200() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/health"))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
