package com.homeney.ticketsystem.service;

import com.homeney.ticketsystem.dto.MetricsResponseDto;
import com.homeney.ticketsystem.model.Ticket;
import com.homeney.ticketsystem.model.TicketPriority;
import com.homeney.ticketsystem.model.TicketStatus;
import com.homeney.ticketsystem.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MetricsService {

    private final TicketRepository ticketRepository;

    public MetricsService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional(readOnly = true)
    public MetricsResponseDto calculateMetrics() {
        List<Ticket> allTickets = ticketRepository.findAll();
        MetricsResponseDto metrics = new MetricsResponseDto();

        long total = allTickets.size();
        metrics.setTotalTickets(total);

        long open = allTickets.stream().filter(t -> t.getStatus() == TicketStatus.OPEN).count();
        long inProgress = allTickets.stream().filter(t -> t.getStatus() == TicketStatus.IN_PROGRESS).count();
        long resolved = allTickets.stream().filter(t -> t.getStatus() == TicketStatus.RESOLVED).count();
        long closed = allTickets.stream().filter(t -> t.getStatus() == TicketStatus.CLOSED).count();

        metrics.setOpenTickets(open);
        metrics.setInProgressTickets(inProgress);
        metrics.setResolvedTickets(resolved);
        metrics.setClosedTickets(closed);
        metrics.setActiveTickets(open + inProgress);

        long critical = allTickets.stream().filter(t -> t.getPriority() == TicketPriority.CRITICAL).count();
        long criticalActive = allTickets.stream()
                .filter(t -> t.getPriority() == TicketPriority.CRITICAL &&
                        (t.getStatus() == TicketStatus.OPEN || t.getStatus() == TicketStatus.IN_PROGRESS))
                .count();

        metrics.setCriticalTickets(critical);
        metrics.setCriticalActiveTickets(criticalActive);

        // Status Distribution
        Map<String, Long> statusMap = new LinkedHashMap<>();
        statusMap.put("Aberto", open);
        statusMap.put("Em Análise", inProgress);
        statusMap.put("Concluído", resolved);
        statusMap.put("Encerrado", closed);
        metrics.setStatusDistribution(statusMap);

        // Priority Distribution
        Map<String, Long> priorityMap = new LinkedHashMap<>();
        for (TicketPriority p : TicketPriority.values()) {
            long count = allTickets.stream().filter(t -> t.getPriority() == p).count();
            priorityMap.put(p.getDescription(), count);
        }
        metrics.setPriorityDistribution(priorityMap);

        // Category Distribution
        Map<String, Long> categoryMap = allTickets.stream()
                .collect(Collectors.groupingBy(
                        t -> (t.getCategory() != null && !t.getCategory().isBlank()) ? t.getCategory() : "Geral",
                        Collectors.counting()
                ));
        metrics.setCategoryDistribution(categoryMap);

        // SLA Compliance Rate
        if (total == 0) {
            metrics.setSlaComplianceRate(100.0);
            metrics.setAvgResolutionTimeHours(0.0);
        } else {
            LocalDateTime now = LocalDateTime.now();
            long compliantCount = 0;
            for (Ticket t : allTickets) {
                if (t.getSlaDeadline() == null) {
                    compliantCount++;
                    continue;
                }
                if (t.getStatus() == TicketStatus.RESOLVED || t.getStatus() == TicketStatus.CLOSED) {
                    if (t.getResolvedAt() == null || !t.getResolvedAt().isAfter(t.getSlaDeadline())) {
                        compliantCount++;
                    }
                } else {
                    if (!now.isAfter(t.getSlaDeadline())) {
                        compliantCount++;
                    }
                }
            }
            double slaRate = ((double) compliantCount / total) * 100.0;
            metrics.setSlaComplianceRate(round(slaRate, 1));

            // Average Resolution Time (hours)
            List<Ticket> resolvedTickets = allTickets.stream()
                    .filter(t -> (t.getStatus() == TicketStatus.RESOLVED || t.getStatus() == TicketStatus.CLOSED)
                            && t.getResolvedAt() != null && t.getCreatedAt() != null)
                    .toList();

            if (resolvedTickets.isEmpty()) {
                metrics.setAvgResolutionTimeHours(0.0);
            } else {
                double totalHours = resolvedTickets.stream()
                        .mapToDouble(t -> {
                            long seconds = Duration.between(t.getCreatedAt(), t.getResolvedAt()).getSeconds();
                            return Math.max(0.1, seconds / 3600.0);
                        })
                        .sum();
                double avgHours = totalHours / resolvedTickets.size();
                metrics.setAvgResolutionTimeHours(round(avgHours, 1));
            }
        }

        // Volume History for the last 7 days
        List<MetricsResponseDto.DailyVolumeDto> volumeHistory = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String label = date.format(formatter);

            long openedOnDate = allTickets.stream()
                    .filter(t -> t.getCreatedAt() != null && t.getCreatedAt().toLocalDate().isEqual(date))
                    .count();

            long resolvedOnDate = allTickets.stream()
                    .filter(t -> t.getResolvedAt() != null && t.getResolvedAt().toLocalDate().isEqual(date))
                    .count();

            volumeHistory.add(new MetricsResponseDto.DailyVolumeDto(label, openedOnDate, resolvedOnDate));
        }
        metrics.setVolumeHistory(volumeHistory);

        return metrics;
    }

    private double round(double value, int places) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return 0.0;
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
