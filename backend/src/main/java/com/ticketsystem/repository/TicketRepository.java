package com.ticketsystem.repository;

import com.ticketsystem.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    long countByStatus(String status);

    long countByPriority(String priority);

    List<Ticket> findByProjectId(Long projectId);

    List<Ticket> findByAssigneeId(Long assigneeId);

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.project LEFT JOIN FETCH t.assignee ORDER BY t.createdAt DESC")
    List<Ticket> findAllWithDetails();

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.project LEFT JOIN FETCH t.assignee WHERE t.id = :id")
    java.util.Optional<Ticket> findByIdWithDetails(Long id);
}
