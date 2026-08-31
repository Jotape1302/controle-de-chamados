package com.homeney.ticketsystem.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TicketPriority {
    LOW("Baixa", 72),
    MEDIUM("Média", 24),
    HIGH("Alta", 8),
    CRITICAL("Crítica", 2);

    private final String description;
    private final int slaHours;

    TicketPriority(String description, int slaHours) {
        this.description = description;
        this.slaHours = slaHours;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    public int getSlaHours() {
        return slaHours;
    }

    @JsonCreator
    public static TicketPriority fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String normalized = value.trim();
        for (TicketPriority priority : TicketPriority.values()) {
            if (priority.name().equalsIgnoreCase(normalized) || priority.description.equalsIgnoreCase(normalized)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Prioridade desconhecida: " + value);
    }
}
