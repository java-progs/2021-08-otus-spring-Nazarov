package ru.otus.homework.actuator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"management.endpoints.web.exposure.include=health",
        "management.endpoint.health.show-details=always"})
public class ActuatorTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnLibraryHealthIndicator() throws Exception {
        mockMvc.perform(get("/actuator/health/library"))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.details.countBook").exists());
    }
}
