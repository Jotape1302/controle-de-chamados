package com.homeney.ticketsystem.repository;

import com.homeney.ticketsystem.model.Ticket;
import com.homeney.ticketsystem.model.TicketPriority;
import com.homeney.ticketsystem.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByPriority(TicketPriority priority);

    List<Ticket> findByRequesterIgnoreCase(String requester);

    long countByStatus(TicketStatus status);

    long countByPriority(TicketPriority priority);

    long countByPriorityAndStatusIn(TicketPriority priority, List<TicketStatus> statuses);

    @Query("SELECT t FROM Ticket t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:requester IS NULL OR LOWER(t.requester) LIKE LOWER(CONCAT('%', :requester, '%'))) AND " +
           "(:category IS NULL OR LOWER(t.category) = LOWER(:category)) AND " +
           "(:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.requester) LIKE LOWER(CONCAT('%', :search, '%')) OR str(t.id) LIKE CONCAT('%', :search, '%')) " +
           "ORDER BY t.id DESC")
    List<Ticket> searchTickets(
            @Param("status") TicketStatus status,
            @Param("priority") TicketPriority priority,
            @Param("requester") String requester,
            @Param("category") String category,
            @Param("search") String search
    );

    @Query("SELECT t FROM Ticket t WHERE t.resolvedAt IS NOT NULL AND t.createdAt IS NOT NULL")
    List<Ticket> findResolvedTicketsWithDates();

    @Query("SELECT t FROM Ticket t WHERE t.createdAt >= :startDate ORDER BY t.createdAt ASC")
    List<Ticket> findTicketsSince(@Param("startDate") LocalDateTime startDate);
}
