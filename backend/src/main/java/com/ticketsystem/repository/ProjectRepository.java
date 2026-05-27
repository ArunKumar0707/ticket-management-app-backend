package com.ticketsystem.repository;

import com.ticketsystem.entity.Project;
import com.ticketsystem.entity.Project.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByProjectCode(String projectCode);
    boolean existsByProjectCodeAndIdNot(String projectCode, Long id);

    List<Project> findByStatus(ProjectStatus status);

    @Query("SELECT p FROM Project p WHERE " +
           "(:search IS NULL OR LOWER(p.projectName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.projectCode) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<Project> searchProjects(
            @Param("search") String search,
            @Param("status") ProjectStatus status,
            Pageable pageable);
}
