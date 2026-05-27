package com.ticketsystem.repository;

import com.ticketsystem.entity.SlaConfig;
import com.ticketsystem.entity.Ticket.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SlaConfigRepository extends JpaRepository<SlaConfig, Long> {
    Optional<SlaConfig> findByPriority(Priority priority);
    boolean existsByPriority(Priority priority);
    boolean existsByPriorityAndIdNot(Priority priority, Long id);
}
