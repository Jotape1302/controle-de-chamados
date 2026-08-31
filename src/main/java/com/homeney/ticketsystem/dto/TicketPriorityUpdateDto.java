package com.homeney.ticketsystem.dto;

import jakarta.validation.constraints.NotBlank;

public class TicketPriorityUpdateDto {

    @NotBlank(message = "A prioridade é obrigatória.")
    private String priority;

    public TicketPriorityUpdateDto() {
    }

    public TicketPriorityUpdateDto(String priority) {
        this.priority = priority;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
