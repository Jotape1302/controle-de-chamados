package com.homeney.ticketsystem.service;

import com.homeney.ticketsystem.dto.TicketRequestDto;
import com.homeney.ticketsystem.dto.TicketResponseDto;
import com.homeney.ticketsystem.exception.ResourceNotFoundException;
import com.homeney.ticketsystem.model.Ticket;
import com.homeney.ticketsystem.model.TicketPriority;
import com.homeney.ticketsystem.model.TicketStatus;
import com.homeney.ticketsystem.repository.TicketRepository;
import com.homeney.ticketsystem.security.Role;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional(readOnly = true)
    public List<TicketResponseDto> getTickets(String statusStr, String priorityStr, String requester, String category, String search) {
        TicketStatus status = null;
        if (statusStr != null && !statusStr.isBlank() && !"ALL".equalsIgnoreCase(statusStr)) {
            status = TicketStatus.fromString(statusStr);
        }

        TicketPriority priority = null;
        if (priorityStr != null && !priorityStr.isBlank() && !"ALL".equalsIgnoreCase(priorityStr)) {
            priority = TicketPriority.fromString(priorityStr);
        }

        String searchClean = (search != null && !search.isBlank()) ? search.trim() : null;
        String requesterClean = (requester != null && !requester.isBlank()) ? requester.trim() : null;
        String categoryClean = (category != null && !category.isBlank() && !"ALL".equalsIgnoreCase(category)) ? category.trim() : null;

        List<Ticket> tickets = ticketRepository.searchTickets(status, priority, requesterClean, categoryClean, searchClean);

        return tickets.stream()
                .map(TicketResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TicketResponseDto getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado com ID " + id + " não foi encontrado."));
        return TicketResponseDto.fromEntity(ticket);
    }

    @Transactional
    public TicketResponseDto createTicket(TicketRequestDto request, String authenticatedUsername) {
        TicketPriority priority = TicketPriority.LOW;
        if (request.getPriority() != null && !request.getPriority().isBlank()) {
            priority = TicketPriority.fromString(request.getPriority());
        }

        String category = (request.getCategory() != null && !request.getCategory().isBlank()) 
                ? request.getCategory() 
                : "Geral";

        Ticket ticket = new Ticket(
                request.getTitle(),
                request.getDescription(),
                priority,
                (request.getRequester() == null || request.getRequester().isBlank())
                        ? authenticatedUsername
                        : request.getRequester().trim(),
                category
        );

        if (request.getAssignedTo() != null && !request.getAssignedTo().isBlank()) {
            ticket.setAssignedTo(request.getAssignedTo().trim());
        }

        Ticket saved = ticketRepository.save(ticket);
        return TicketResponseDto.fromEntity(saved);
    }

    @Transactional
    public TicketResponseDto updateTicket(Long id, TicketRequestDto request, Authentication authentication) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado com ID " + id + " não foi encontrado."));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            ticket.setTitle(request.getTitle());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            ticket.setDescription(request.getDescription());
        }
        if (request.getPriority() != null && !request.getPriority().isBlank()) {
            TicketPriority newPriority = TicketPriority.fromString(request.getPriority());
            if (newPriority != ticket.getPriority() && !Role.isAdmin(authentication)) {
                throw new AccessDeniedException("Apenas administradores podem alterar a prioridade.");
            }
            ticket.setPriority(newPriority);
        }
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            ticket.setCategory(request.getCategory());
        }
        if (request.getRequester() != null && !request.getRequester().isBlank()) {
            ticket.setRequester(request.getRequester());
        }
        if (request.getAssignedTo() != null) {
            ticket.setAssignedTo(request.getAssignedTo().isBlank() ? null : request.getAssignedTo().trim());
        }

        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket updated = ticketRepository.save(ticket);
        return TicketResponseDto.fromEntity(updated);
    }

    @Transactional
    public TicketResponseDto updateStatus(Long id, String newStatusStr) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado com ID " + id + " não foi encontrado."));

        TicketStatus newStatus = TicketStatus.fromString(newStatusStr);
        ticket.setStatus(newStatus);
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket updated = ticketRepository.save(ticket);
        return TicketResponseDto.fromEntity(updated);
    }

    @Transactional
    public TicketResponseDto updatePriority(Long id, String newPriorityStr) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado com ID " + id + " não foi encontrado."));

        TicketPriority newPriority = TicketPriority.fromString(newPriorityStr);
        ticket.setPriority(newPriority);
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket updated = ticketRepository.save(ticket);
        return TicketResponseDto.fromEntity(updated);
    }

    @Transactional
    public void deleteTicket(Long id, Authentication authentication) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado com ID " + id + " não foi encontrado."));

        boolean isRequester = ticket.getRequester().equals(authentication.getName());
        if (!Role.isAdmin(authentication) && !isRequester) {
            throw new AccessDeniedException("Apenas administradores ou o solicitante podem excluir este chamado.");
        }
        ticketRepository.delete(ticket);
    }
}
