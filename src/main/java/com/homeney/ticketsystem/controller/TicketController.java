package com.homeney.ticketsystem.controller;

import com.homeney.ticketsystem.dto.TicketPriorityUpdateDto;
import com.homeney.ticketsystem.dto.TicketRequestDto;
import com.homeney.ticketsystem.dto.TicketResponseDto;
import com.homeney.ticketsystem.dto.TicketStatusUpdateDto;
import com.homeney.ticketsystem.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Chamados (Tickets)", description = "Endpoints para gerenciamento do ciclo de vida de chamados e tickets de suporte")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    @Operation(summary = "Listar chamados", description = "Retorna todos os chamados com suporte a filtros por status, prioridade, solicitante, categoria e busca textual")
    public ResponseEntity<List<TicketResponseDto>> getTickets(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String requester,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search
    ) {
        List<TicketResponseDto> tickets = ticketService.getTickets(status, priority, requester, category, search);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar chamado por ID", description = "Retorna os detalhes completos de um chamado específico")
    public ResponseEntity<TicketResponseDto> getTicketById(@PathVariable Long id) {
        TicketResponseDto ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping
    @Operation(summary = "Abrir novo chamado", description = "Cria um novo chamado. Se o solicitante não for informado, usa o usuário autenticado.")
    public ResponseEntity<TicketResponseDto> createTicket(
            @Valid @RequestBody TicketRequestDto request,
            Authentication authentication
    ) {
        TicketResponseDto created = ticketService.createTicket(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar chamado completo", description = "Atualiza todas as informações do chamado identificado por ID")
    public ResponseEntity<TicketResponseDto> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketRequestDto request,
            Authentication authentication
    ) {
        TicketResponseDto updated = ticketService.updateTicket(id, request, authentication);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status do chamado", description = "Atualiza de forma rápida o status do chamado (ex: Aberto, Em Análise, Concluído, Encerrado)")
    public ResponseEntity<TicketResponseDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody TicketStatusUpdateDto statusDto
    ) {
        TicketResponseDto updated = ticketService.updateStatus(id, statusDto.getStatus());
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/priority")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Alterar prioridade do chamado", description = "Atualiza a prioridade do chamado (Baixa, Média, Alta, Crítica). Requer perfil ADMIN.")
    public ResponseEntity<TicketResponseDto> updatePriority(
            @PathVariable Long id,
            @Valid @RequestBody TicketPriorityUpdateDto priorityDto
    ) {
        TicketResponseDto updated = ticketService.updatePriority(id, priorityDto.getPriority());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir chamado", description = "Remove um chamado. Permitido para ADMIN ou para o próprio solicitante.")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id, Authentication authentication) {
        ticketService.deleteTicket(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
