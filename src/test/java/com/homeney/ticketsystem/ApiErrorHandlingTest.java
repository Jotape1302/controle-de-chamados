package com.homeney.ticketsystem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Garante o contrato de erros da API: {timestamp, status, error, message, errors?}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "operador")
class ApiErrorHandlingTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("validação retorna 400 com erros por campo")
    void validationErrors() throws Exception {
        mvc.perform(post("/api/tickets").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.title").exists())
                .andExpect(jsonPath("$.errors.description").exists())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("chamado inexistente retorna 404")
    void notFound() throws Exception {
        mvc.perform(get("/api/tickets/{id}", 999_999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Chamado com ID 999999 não foi encontrado."));
    }

    @Test
    @DisplayName("JSON mal formatado retorna 400 sem detalhes internos")
    void malformedJson() throws Exception {
        mvc.perform(post("/api/tickets").contentType(MediaType.APPLICATION_JSON).content("{quebrado"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Corpo da requisição inválido ou mal formatado."));
    }

    @Test
    @DisplayName("ID com tipo inválido retorna 400")
    void typeMismatch() throws Exception {
        mvc.perform(get("/api/tickets/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Parâmetro 'id' com valor inválido."));
    }

    @Test
    @DisplayName("status desconhecido retorna 400")
    void unknownStatus() throws Exception {
        mvc.perform(get("/api/tickets").param("status", "Inexistente"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("prioridade inválida retorna 400")
    void invalidPriority() throws Exception {
        mvc.perform(patch("/api/tickets/{id}/priority", 1)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"priority\":\"Urgentíssima\"}"))
                .andExpect(status().is4xxClientError());
    }
}
