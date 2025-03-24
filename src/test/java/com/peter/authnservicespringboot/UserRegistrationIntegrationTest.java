package com.peter.authnservicespringboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peter.authnservicespringboot.domain.dto.UserRegistrationRequest;
import com.peter.authnservicespringboot.repository.UserRepository;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("ci")
public class UserRegistrationIntegrationTest {

    private static final String REGISTER_API_PATH = "/api/v1/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Flyway flyway;

    private UserRegistrationRequest validRequest;
    private String firstName;
    private String lastName;
    private String testEmail;

    @BeforeEach
    void setUp() {
        firstName = "John";
        lastName = "Doe";
        testEmail = "test@example.com";
        validRequest = new UserRegistrationRequest(
                firstName,
                lastName,
                testEmail,
                "Password123!"
        );

        // Reset database before each test
        flyway.clean();
        flyway.migrate();
    }

    @AfterEach
    void cleanup() {
        userRepository.deleteAll();
    }

    /**
     * Test normal registration flow
     */
    @Test
    void whenValidInput_thenReturns201() throws Exception {
        mockMvc.perform(post(REGISTER_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(validRequest.email()));

        assertTrue(userRepository.findByEmail(validRequest.email()).isPresent());
    }

    /**
     * Test empty first name
     */
    @Test
    void whenEmptyFirstName_thenReturns400() throws Exception {
        UserRegistrationRequest invalidRequest = new UserRegistrationRequest(
                "",
                lastName,
                testEmail,
                "Password123!"
        );

        mockMvc.perform(post(REGISTER_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        assertEquals(0, userRepository.count());
    }

    /**
     * Test empty last name
     */
    @Test
    void whenEmptyLastName_thenReturns400() throws Exception {
        UserRegistrationRequest invalidRequest = new UserRegistrationRequest(
                firstName,
                "",
                testEmail,
                "Password123!"
        );

        mockMvc.perform(post(REGISTER_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        assertEquals(0, userRepository.count());
    }

    /**
     * Test invalid email format
     */
    @Test
    void whenInvalidEmail_thenReturns400() throws Exception {
        UserRegistrationRequest invalidRequest = new UserRegistrationRequest(
                firstName,
                lastName,
                "invalid-email",
                "Password123!"
        );

        mockMvc.perform(post(REGISTER_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        assertFalse(userRepository.findByEmail(invalidRequest.email()).isPresent());
    }

    /**
     * Test duplicate registration
     */
    @Test
    void whenDuplicateEmail_thenReturns409() throws Exception {
        // First registration
        mockMvc.perform(post(REGISTER_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated());

        // Attempt to register with the same email again
        mockMvc.perform(post(REGISTER_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict());

        assertEquals(1, userRepository.count());
    }

    /**
     * Test empty email
     */
    @Test
    void whenEmptyEmail_thenReturns400() throws Exception {
        UserRegistrationRequest invalidRequest = new UserRegistrationRequest(
                firstName,
                lastName,
                "",
                "Password123!"
        );

        mockMvc.perform(post(REGISTER_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        assertEquals(0, userRepository.count());
    }

    /**
     * Test empty password
     */
    @Test
    void whenEmptyPassword_thenReturns400() throws Exception {
        UserRegistrationRequest invalidRequest = new UserRegistrationRequest(
                firstName,
                lastName,
                "test@example.com",
                ""
        );

        mockMvc.perform(post(REGISTER_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        assertEquals(0, userRepository.count());
    }
}