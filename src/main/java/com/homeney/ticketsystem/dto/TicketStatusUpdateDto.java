package com.homeney.ticketsystem.dto;

import jakarta.validation.constraints.NotBlank;

public class TicketStatusUpdateDto {

    @NotBlank(message = "O status é obrigatório.")
    private String status;

    public TicketStatusUpdateDto() {
    }

    public TicketStatusUpdateDto(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
