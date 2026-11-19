package com.homeney.ticketsystem;

import com.homeney.ticketsystem.dto.MetricsResponseDto;
import com.homeney.ticketsystem.dto.TicketRequestDto;
import com.homeney.ticketsystem.dto.TicketResponseDto;
import com.homeney.ticketsystem.model.TicketPriority;
import com.homeney.ticketsystem.model.TicketStatus;
import com.homeney.ticketsystem.service.MetricsService;
import com.homeney.ticketsystem.service.TicketService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private MetricsService metricsService;

    @Test
    @DisplayName("Deve criar e consultar um chamado com sucesso")
    void testCreateAndGetTicket() {
        TicketRequestDto request = new TicketRequestDto(
                "Teste de Conectividade",
                "Descrição detalhada do chamado de teste para homologação.",
                "Alta",
                "Infraestrutura",
                "admin",
                "jotape"
        );

        TicketResponseDto created = ticketService.createTicket(request, "admin");
        assertNotNull(created.getId());
        assertEquals("Teste de Conectividade", created.getTitle());
        assertEquals("Alta", created.getPriority());
        assertEquals("Aberto", created.getStatus());
        assertEquals("admin", created.getRequester());
        assertEquals("jotape", created.getAssignedTo());

        TicketResponseDto fetched = ticketService.getTicketById(created.getId());
        assertEquals(created.getId(), fetched.getId());
        assertEquals("Teste de Conectividade", fetched.getTitle());
    }

    @Test
    @DisplayName("Deve atualizar status e prioridade do chamado")
    void testUpdateStatusAndPriority() {
        TicketRequestDto request = new TicketRequestDto(
                "Bug no frontend",
                "Erro de renderização",
                "Baixa",
                "Software",
                "operador",
                null
        );

        TicketResponseDto created = ticketService.createTicket(request, "admin");
        
        TicketResponseDto inProgress = ticketService.updateStatus(created.getId(), "Em Análise");
        assertEquals("Em Análise", inProgress.getStatus());

        TicketResponseDto resolved = ticketService.updateStatus(created.getId(), "Concluído");
        assertEquals("Concluído", resolved.getStatus());
        assertNotNull(resolved.getResolvedAt());

        TicketResponseDto updatedPriority = ticketService.updatePriority(created.getId(), "Crítica");
        assertEquals("Crítica", updatedPriority.getPriority());
    }

    @Test
    @DisplayName("Deve calcular métricas corretamente")
    void testMetricsCalculation() {
        MetricsResponseDto metrics = metricsService.calculateMetrics();
        assertNotNull(metrics);
        assertTrue(metrics.getTotalTickets() >= 0);
        assertNotNull(metrics.getStatusDistribution());
        assertNotNull(metrics.getPriorityDistribution());
        assertNotNull(metrics.getVolumeHistory());
    }
}
