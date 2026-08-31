package com.homeney.ticketsystem.dto;

import java.util.List;
import java.util.Map;

public class MetricsResponseDto {

    private long totalTickets;
    private long openTickets;
    private long inProgressTickets;
    private long resolvedTickets;
    private long closedTickets;
    private long activeTickets;
    private long criticalTickets;
    private long criticalActiveTickets;
    private double avgResolutionTimeHours;
    private double slaComplianceRate;
    private Map<String, Long> statusDistribution;
    private Map<String, Long> priorityDistribution;
    private Map<String, Long> categoryDistribution;
    private List<DailyVolumeDto> volumeHistory;

    public static class DailyVolumeDto {
        private String date;
        private long opened;
        private long resolved;

        public DailyVolumeDto() {
        }

        public DailyVolumeDto(String date, long opened, long resolved) {
            this.date = date;
            this.opened = opened;
            this.resolved = resolved;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public long getOpened() {
            return opened;
        }

        public void setOpened(long opened) {
            this.opened = opened;
        }

        public long getResolved() {
            return resolved;
        }

        public void setResolved(long resolved) {
            this.resolved = resolved;
        }
    }

    public MetricsResponseDto() {
    }

    public long getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(long totalTickets) {
        this.totalTickets = totalTickets;
    }

    public long getOpenTickets() {
        return openTickets;
    }

    public void setOpenTickets(long openTickets) {
        this.openTickets = openTickets;
    }

    public long getInProgressTickets() {
        return inProgressTickets;
    }

    public void setInProgressTickets(long inProgressTickets) {
        this.inProgressTickets = inProgressTickets;
    }

    public long getResolvedTickets() {
        return resolvedTickets;
    }

    public void setResolvedTickets(long resolvedTickets) {
        this.resolvedTickets = resolvedTickets;
    }

    public long getClosedTickets() {
        return closedTickets;
    }

    public void setClosedTickets(long closedTickets) {
        this.closedTickets = closedTickets;
    }

    public long getActiveTickets() {
        return activeTickets;
    }

    public void setActiveTickets(long activeTickets) {
        this.activeTickets = activeTickets;
    }

    public long getCriticalTickets() {
        return criticalTickets;
    }

    public void setCriticalTickets(long criticalTickets) {
        this.criticalTickets = criticalTickets;
    }

    public long getCriticalActiveTickets() {
        return criticalActiveTickets;
    }

    public void setCriticalActiveTickets(long criticalActiveTickets) {
        this.criticalActiveTickets = criticalActiveTickets;
    }

    public double getAvgResolutionTimeHours() {
        return avgResolutionTimeHours;
    }

    public void setAvgResolutionTimeHours(double avgResolutionTimeHours) {
        this.avgResolutionTimeHours = avgResolutionTimeHours;
    }

    public double getSlaComplianceRate() {
        return slaComplianceRate;
    }

    public void setSlaComplianceRate(double slaComplianceRate) {
        this.slaComplianceRate = slaComplianceRate;
    }

    public Map<String, Long> getStatusDistribution() {
        return statusDistribution;
    }

    public void setStatusDistribution(Map<String, Long> statusDistribution) {
        this.statusDistribution = statusDistribution;
    }

    public Map<String, Long> getPriorityDistribution() {
        return priorityDistribution;
    }

    public void setPriorityDistribution(Map<String, Long> priorityDistribution) {
        this.priorityDistribution = priorityDistribution;
    }

    public Map<String, Long> getCategoryDistribution() {
        return categoryDistribution;
    }

    public void setCategoryDistribution(Map<String, Long> categoryDistribution) {
        this.categoryDistribution = categoryDistribution;
    }

    public List<DailyVolumeDto> getVolumeHistory() {
        return volumeHistory;
    }

    public void setVolumeHistory(List<DailyVolumeDto> volumeHistory) {
        this.volumeHistory = volumeHistory;
    }
}
