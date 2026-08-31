package com.homeney.ticketsystem.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TicketStatus {
    OPEN("Aberto"),
    IN_PROGRESS("Em Análise"),
    RESOLVED("Concluído"),
    CLOSED("Encerrado");

    private final String description;

    TicketStatus(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static TicketStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String normalized = value.trim();
        for (TicketStatus status : TicketStatus.values()) {
            if (status.name().equalsIgnoreCase(normalized) || status.description.equalsIgnoreCase(normalized)) {
                return status;
            }
        }
        if ("Andamento".equalsIgnoreCase(normalized) || "Em Andamento".equalsIgnoreCase(normalized)) {
            return IN_PROGRESS;
        }
        throw new IllegalArgumentException("Status desconhecido: " + value);
    }
}
