package com.homeney.ticketsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health Check", description = "Monitoramento do status do serviço e da API")
public class HealthController {

    @GetMapping
    @Operation(summary = "Verificar integridade da API", description = "Retorna status operacional, versão e timestamp do servidor")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "ONLINE",
                "service", "homeney-tickets",
                "version", "2.0.0",
                "timestamp", LocalDateTime.now()
        ));
    }
}
