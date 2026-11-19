package com.homeney.ticketsystem;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeney.ticketsystem.model.Ticket;
import com.homeney.ticketsystem.model.TicketPriority;
import com.homeney.ticketsystem.model.User;
import com.homeney.ticketsystem.repository.TicketRepository;
import com.homeney.ticketsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    private static final String ADMIN_PASSWORD = "admin-pass-123";
    private static final String USER_PASSWORD = "user-pass-123";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
        userRepository.deleteAll();
        userRepository.save(new User("admin", passwordEncoder.encode(ADMIN_PASSWORD), "Admin", "admin@test.local", true));
        userRepository.save(new User("operador", passwordEncoder.encode(USER_PASSWORD), "Operador", "op@test.local", false));
        userRepository.save(new User("outro", passwordEncoder.encode(USER_PASSWORD), "Outro", "outro@test.local", false));
    }

    private String login(String username, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(body);
        return "Bearer " + json.get("accessToken").asText();
    }

    private Ticket ticketRequestedBy(String requester) {
        return ticketRepository.save(new Ticket("Título", "Descrição", TicketPriority.LOW, requester, "Geral"));
    }

    @Nested
    @DisplayName("Autenticação")
    class Authentication {

        @Test
        @DisplayName("login válido retorna token Bearer e dados do usuário sem a senha")
        void validLogin() throws Exception {
            mvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"admin\",\"password\":\"" + ADMIN_PASSWORD + "\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.user.username").value("admin"))
                    .andExpect(jsonPath("$.user.admin").value(true))
                    .andExpect(jsonPath("$.user.password").doesNotExist());
        }

        @Test
        @DisplayName("senha errada retorna 401 com mensagem genérica")
        void wrongPassword() throws Exception {
            mvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"admin\",\"password\":\"errada\"}"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Usuário ou senha inválidos."));
        }

        @Test
        @DisplayName("usuário inexistente retorna a mesma resposta que senha errada")
        void unknownUser() throws Exception {
            mvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"fantasma\",\"password\":\"qualquer\"}"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Usuário ou senha inválidos."));
        }

        @Test
        @DisplayName("senhas são armazenadas com hash BCrypt")
        void passwordsAreHashed() {
            String stored = userRepository.findByUsername("admin").orElseThrow().getPassword();
            assertThat(stored).isNotEqualTo(ADMIN_PASSWORD).startsWith("$2");
        }

        @Test
        @DisplayName("endpoints protegidos exigem token")
        void protectedEndpointsRequireToken() throws Exception {
            mvc.perform(get("/api/tickets")).andExpect(status().isUnauthorized());
            mvc.perform(get("/api/metrics")).andExpect(status().isUnauthorized());
            mvc.perform(get("/api/users")).andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("token adulterado é rejeitado")
        void tamperedToken() throws Exception {
            String token = login("operador", USER_PASSWORD);
            mvc.perform(get("/api/tickets").header(HttpHeaders.AUTHORIZATION, token + "x"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("health e documentação são públicos")
        void publicEndpoints() throws Exception {
            mvc.perform(get("/api/health")).andExpect(status().isOk());
            mvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
        }

        @Test
        @DisplayName("/api/auth/me retorna o dono do token")
        void me() throws Exception {
            mvc.perform(get("/api/auth/me").header(HttpHeaders.AUTHORIZATION, login("operador", USER_PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("operador"))
                    .andExpect(jsonPath("$.admin").value(false));
        }
    }

    @Nested
    @DisplayName("Autorização")
    class Authorization {

        @Test
        @DisplayName("operador não pode alterar prioridade; admin pode")
        void priorityRequiresAdmin() throws Exception {
            Ticket ticket = ticketRequestedBy("operador");
            String body = "{\"priority\":\"Crítica\"}";

            mvc.perform(patch("/api/tickets/{id}/priority", ticket.getId())
                            .header(HttpHeaders.AUTHORIZATION, login("operador", USER_PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isForbidden());

            mvc.perform(patch("/api/tickets/{id}/priority", ticket.getId())
                            .header(HttpHeaders.AUTHORIZATION, login("admin", ADMIN_PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.priority").value("Crítica"));
        }

        @Test
        @DisplayName("operador não consegue burlar a regra de prioridade via PUT")
        void priorityViaPutRequiresAdmin() throws Exception {
            Ticket ticket = ticketRequestedBy("operador");
            mvc.perform(put("/api/tickets/{id}", ticket.getId())
                            .header(HttpHeaders.AUTHORIZATION, login("operador", USER_PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\":\"T\",\"description\":\"D\",\"priority\":\"Crítica\"}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("solicitante pode excluir o próprio chamado, mas não o de outro usuário")
        void deleteRules() throws Exception {
            Ticket own = ticketRequestedBy("operador");
            Ticket others = ticketRequestedBy("outro");
            String token = login("operador", USER_PASSWORD);

            mvc.perform(delete("/api/tickets/{id}", others.getId()).header(HttpHeaders.AUTHORIZATION, token))
                    .andExpect(status().isForbidden());
            mvc.perform(delete("/api/tickets/{id}", own.getId()).header(HttpHeaders.AUTHORIZATION, token))
                    .andExpect(status().isNoContent());
            mvc.perform(delete("/api/tickets/{id}", others.getId())
                            .header(HttpHeaders.AUTHORIZATION, login("admin", ADMIN_PASSWORD)))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("apenas admin cadastra usuários, e a senha é validada")
        void createUserRules() throws Exception {
            String newUser = "{\"username\":\"novo\",\"password\":\"senha-forte-1\",\"admin\":true}";

            mvc.perform(post("/api/users")
                            .header(HttpHeaders.AUTHORIZATION, login("operador", USER_PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON).content(newUser))
                    .andExpect(status().isForbidden());

            String adminToken = login("admin", ADMIN_PASSWORD);
            mvc.perform(post("/api/users")
                            .header(HttpHeaders.AUTHORIZATION, adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"fraco\",\"password\":\"123\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.password").exists());

            mvc.perform(post("/api/users")
                            .header(HttpHeaders.AUTHORIZATION, adminToken)
                            .contentType(MediaType.APPLICATION_JSON).content(newUser))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.username").value("novo"))
                    .andExpect(jsonPath("$.password").doesNotExist());
        }

        @Test
        @DisplayName("chamado sem solicitante usa o usuário autenticado")
        void requesterDefaultsToCurrentUser() throws Exception {
            mvc.perform(post("/api/tickets")
                            .header(HttpHeaders.AUTHORIZATION, login("operador", USER_PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\":\"Sem solicitante\",\"description\":\"Teste\"}"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.requester").value("operador"));
        }
    }

    @Nested
    @DisplayName("CORS")
    class Cors {

        @Test
        @DisplayName("origem configurada é aceita e origem desconhecida é bloqueada")
        void corsOrigins() throws Exception {
            mvc.perform(options("/api/tickets")
                            .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"));

            mvc.perform(options("/api/tickets")
                            .header(HttpHeaders.ORIGIN, "https://site-malicioso.example")
                            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                    .andExpect(status().isForbidden());
        }
    }
}
