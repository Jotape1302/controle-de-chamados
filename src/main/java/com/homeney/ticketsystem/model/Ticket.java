package com.homeney.ticketsystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketPriority priority;

    @Column(nullable = false, length = 50)
    private String requester;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(length = 50)
    private String assignedTo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime resolvedAt;

    @Column
    private LocalDateTime slaDeadline;

    public Ticket() {
        this.status = TicketStatus.OPEN;
        this.priority = TicketPriority.LOW;
        this.category = "Geral";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Ticket(String title, String description, TicketPriority priority, String requester, String category) {
        this.title = title;
        this.description = description;
        this.status = TicketStatus.OPEN;
        this.priority = (priority != null) ? priority : TicketPriority.LOW;
        this.requester = requester;
        this.category = (category != null && !category.isBlank()) ? category : "Geral";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.slaDeadline = this.createdAt.plusHours(this.priority.getSlaHours());
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = TicketStatus.OPEN;
        }
        if (this.priority == null) {
            this.priority = TicketPriority.LOW;
        }
        if (this.category == null || this.category.isBlank()) {
            this.category = "Geral";
        }
        if (this.slaDeadline == null && this.priority != null) {
            this.slaDeadline = this.createdAt.plusHours(this.priority.getSlaHours());
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
        if ((status == TicketStatus.RESOLVED || status == TicketStatus.CLOSED) && this.resolvedAt == null) {
            this.resolvedAt = LocalDateTime.now();
        } else if (status == TicketStatus.OPEN || status == TicketStatus.IN_PROGRESS) {
            this.resolvedAt = null;
        }
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
        if (this.createdAt != null && priority != null) {
            this.slaDeadline = this.createdAt.plusHours(priority.getSlaHours());
        }
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
}
