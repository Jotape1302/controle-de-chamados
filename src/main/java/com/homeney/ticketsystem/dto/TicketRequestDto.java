package com.homeney.ticketsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TicketRequestDto {

    @NotBlank(message = "O título é obrigatório.")
    @Size(max = 150, message = "O título deve ter no máximo 150 caracteres.")
    private String title;

    @NotBlank(message = "A descrição é obrigatória.")
    @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres.")
    private String description;

    private String priority; // Baixa, Média, Alta, Crítica or LOW, MEDIUM...

    private String category;

    @NotBlank(message = "O solicitante é obrigatório.")
    private String requester;

    private String assignedTo;

    public TicketRequestDto() {
    }

    public TicketRequestDto(String title, String description, String priority, String category, String requester, String assignedTo) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.category = category;
        this.requester = requester;
        this.assignedTo = assignedTo;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
}
