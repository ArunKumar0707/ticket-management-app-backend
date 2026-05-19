package com.ticketsystem.repository;

import com.ticketsystem.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    long countByProjectStatus(String status);
    List<Project> findByProjectStatus(String status);
    List<Project> findByProjectNameContainingIgnoreCase(String name);
}
