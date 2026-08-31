package com.homeney.ticketsystem.controller;

import com.homeney.ticketsystem.dto.MetricsResponseDto;
import com.homeney.ticketsystem.service.MetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
@Tag(name = "Métricas (HUD Analytics)", description = "Endpoints para recuperação de KPIs, estatísticas e volumetria de suporte em tempo real")
@CrossOrigin(origins = "*")
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping
    @Operation(summary = "Obter métricas do sistema", description = "Retorna contadores de chamados ativos, críticos, taxa de SLA, tempo médio de resposta e distribuições")
    public ResponseEntity<MetricsResponseDto> getMetrics() {
        MetricsResponseDto metrics = metricsService.calculateMetrics();
        return ResponseEntity.ok(metrics);
    }
}
