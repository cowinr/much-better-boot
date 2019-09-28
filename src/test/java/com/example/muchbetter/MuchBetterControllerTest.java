package com.example.muchbetter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MuchBetterControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private TokenGenerator tokenGenerator;

    @Test
    public void userIsCreatedAndReturnsToken() throws Exception {
        Mockito.when(tokenGenerator.generateToken()).thenReturn("dummy_token");

        this.mvc.perform(post("/login"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Authorization", "dummy_token"));
    }

    @Test
    public void sendTransactionForValidTokenReturnsCreated() throws Exception {
        User test = new User("test_token", new BigDecimal("100"), "GBP");
        userRepository.save(test);

        Transaction transaction = new Transaction(LocalDateTime.now(), "Item desc", new BigDecimal("1.24"), "GBP");
        this.mvc.perform(post("/spend")
                .header("Authorization", "Bearer test_token")
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonMapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated());
    }

    @Test
    public void sendTransactionForInvalidTokenReturnsForbidden() throws Exception {
        Transaction transaction = new Transaction(LocalDateTime.now(), "Item desc", new BigDecimal("1.24"), "GBP");
        this.mvc.perform(post("/spend")
                .header("Authorization", "Bearer random_token")
                .contentType(APPLICATION_JSON_UTF8)
                .content(jsonMapper.writeValueAsString(transaction)))
                .andExpect(status().isForbidden());
    }

}