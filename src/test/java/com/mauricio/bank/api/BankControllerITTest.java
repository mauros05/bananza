package com.mauricio.bank.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BankControllerITTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void createAccount_thenDeposit_thenGetAccount() throws Exception {
        // Crear cuenta
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "accountNumber": "001",
                          "ownerName": "Mauricio",
                          "initialBalance": 1000.00
                        }
               """)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountNumber").value("001"))
                    .andExpect(jsonPath("$.balance").value(1000.00));

        // Depositar
        mockMvc.perform(post("/api/accounts/001/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "amount": 200.00 }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1200.00));

        // Consular
        mockMvc.perform(get("/api/accounts/001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1200.00));

    }

    @Test
    void withdraw_withoutFunds_returns422() throws Exception {

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                  {
                    "accountNumber": "010",
                    "ownerName": "A",
                    "initialBalance": 10.00
                  }
                """)).andExpect(status().isOk());

        mockMvc.perform(post("/api/accounts/010/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                         { "amount":  999.00 }
                        """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").exists());

    }
}
