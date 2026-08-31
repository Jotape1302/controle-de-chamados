package com.homeney.ticketsystem.dto;

import com.homeney.ticketsystem.model.Ticket;
import com.homeney.ticketsystem.model.TicketStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class TicketResponseDto {

    private Long id;
    private String title;
    private String description;
    private String status;
    private String statusKey;
    private String priority;
    private String priorityKey;
    private String requester;
    private String category;
    private String assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime slaDeadline;
    private boolean slaBreached;
    private long slaRemainingMinutes;

    public TicketResponseDto() {
    }

    public static TicketResponseDto fromEntity(Ticket ticket) {
        if (ticket == null) return null;

        TicketResponseDto dto = new TicketResponseDto();
        dto.setId(ticket.getId());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setStatus(ticket.getStatus() != null ? ticket.getStatus().getDescription() : "Aberto");
        dto.setStatusKey(ticket.getStatus() != null ? ticket.getStatus().name() : "OPEN");
        dto.setPriority(ticket.getPriority() != null ? ticket.getPriority().getDescription() : "Baixa");
        dto.setPriorityKey(ticket.getPriority() != null ? ticket.getPriority().name() : "LOW");
        dto.setRequester(ticket.getRequester());
        dto.setCategory(ticket.getCategory());
        dto.setAssignedTo(ticket.getAssignedTo());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        dto.setResolvedAt(ticket.getResolvedAt());
        dto.setSlaDeadline(ticket.getSlaDeadline());

        LocalDateTime now = LocalDateTime.now();
        if (ticket.getStatus() == TicketStatus.RESOLVED || ticket.getStatus() == TicketStatus.CLOSED) {
            if (ticket.getResolvedAt() != null && ticket.getSlaDeadline() != null) {
                dto.setSlaBreached(ticket.getResolvedAt().isAfter(ticket.getSlaDeadline()));
                dto.setSlaRemainingMinutes(0);
            } else {
                dto.setSlaBreached(false);
                dto.setSlaRemainingMinutes(0);
            }
        } else {
            if (ticket.getSlaDeadline() != null) {
                dto.setSlaBreached(now.isAfter(ticket.getSlaDeadline()));
                long minutes = Duration.between(now, ticket.getSlaDeadline()).toMinutes();
                dto.setSlaRemainingMinutes(minutes);
            } else {
                dto.setSlaBreached(false);
                dto.setSlaRemainingMinutes(0);
            }
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusKey() {
        return statusKey;
    }

    public void setStatusKey(String statusKey) {
        this.statusKey = statusKey;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPriorityKey() {
        return priorityKey;
    }

    public void setPriorityKey(String priorityKey) {
        this.priorityKey = priorityKey;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public LocalDateTime getSlaDeadline() {
        return slaDeadline;
    }

    public void setSlaDeadline(LocalDateTime slaDeadline) {
        this.slaDeadline = slaDeadline;
    }

    public boolean isSlaBreached() {
        return slaBreached;
    }

    public void setSlaBreached(boolean slaBreached) {
        this.slaBreached = slaBreached;
    }

    public long getSlaRemainingMinutes() {
        return slaRemainingMinutes;
    }

    public void setSlaRemainingMinutes(long slaRemainingMinutes) {
        this.slaRemainingMinutes = slaRemainingMinutes;
    }
}
