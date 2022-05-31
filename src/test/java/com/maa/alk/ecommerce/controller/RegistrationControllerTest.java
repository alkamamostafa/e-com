package com.maa.alk.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maa.alk.ecommerce.dto.RegistrationRequest;
import com.maa.alk.ecommerce.util.TestConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/sql/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private RegistrationRequest registrationRequest;

    @Before
    public void init() {
        registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("testtest@test.com");
        registrationRequest.setPassword(TestConstants.USER_PASSWORD);
        registrationRequest.setPassword2(TestConstants.USER_PASSWORD);
        registrationRequest.setFirstName(TestConstants.FIRST_NAME);
        registrationRequest.setLastName(TestConstants.LAST_NAME);
        registrationRequest.setCaptcha("12345");
    }

    @Test
    public void registration_ShouldPassword2BeEmpty() throws Exception {
        registrationRequest.setPassword("");
        registrationRequest.setPassword2("");

        mockMvc.perform(MockMvcRequestBuilders.post(TestConstants.URL_REGISTRATION_BASIC)
                        .content(mapper.writeValueAsString(registrationRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password2Error", is("The password confirmation must be between 6 and 16 characters long")));
    }

    @Test
    public void registration_ShouldPasswordsNotMatch() throws Exception {
        registrationRequest.setPassword2("12345678");

        mockMvc.perform(MockMvcRequestBuilders.post(TestConstants.URL_REGISTRATION_BASIC)
                        .content(mapper.writeValueAsString(registrationRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.passwordError", is("Passwords do not match.")));
    }

    @Test
    public void registration_ShouldUserEmailIsExist() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail(TestConstants.USER_EMAIL);
        registrationRequest.setPassword(TestConstants.USER_PASSWORD);
        registrationRequest.setPassword2(TestConstants.USER_PASSWORD);
        registrationRequest.setFirstName(TestConstants.FIRST_NAME);
        registrationRequest.setLastName(TestConstants.LAST_NAME);
        registrationRequest.setCaptcha("12345");

        mockMvc.perform(MockMvcRequestBuilders.post(TestConstants.URL_REGISTRATION_BASIC)
                        .content(mapper.writeValueAsString(registrationRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.emailError").value("Email is already used."));
    }

    @Test
    public void registration_ShouldCaptchaNotFilled() throws Exception {
        registrationRequest.setCaptcha(null);

        mockMvc.perform(MockMvcRequestBuilders.post(TestConstants.URL_REGISTRATION_BASIC)
                        .content(mapper.writeValueAsString(registrationRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.captchaError").value("Fill captcha."));
    }

    @Test
    public void registration_ShouldInputFieldsAreEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(TestConstants.URL_REGISTRATION_BASIC)
                        .param("password2", "")
                        .param("g-recaptcha-response", "")
                        .content(mapper.writeValueAsString(new RegistrationRequest()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void activateEmailCode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(TestConstants.URL_REGISTRATION_ACTIVATE, TestConstants.USER_ACTIVATION_CODE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("User successfully activated.")));
    }

    @Test
    public void activateEmailCode_ShouldNotFoundActivationCode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(TestConstants.URL_REGISTRATION_ACTIVATE, "123")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("Activation code not found.")));
    }
}
