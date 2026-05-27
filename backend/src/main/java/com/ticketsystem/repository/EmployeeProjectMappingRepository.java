package com.ticketsystem.repository;

import com.ticketsystem.entity.EmployeeProjectMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeProjectMappingRepository extends JpaRepository<EmployeeProjectMapping, Long> {
    List<EmployeeProjectMapping> findByEmployeeIdAndIsActiveTrue(Long employeeId);
    List<EmployeeProjectMapping> findByProjectIdAndIsActiveTrue(Long projectId);
    Optional<EmployeeProjectMapping> findByEmployeeIdAndProjectId(Long employeeId, Long projectId);
    boolean existsByEmployeeIdAndProjectId(Long employeeId, Long projectId);

    @Query("SELECT m FROM EmployeeProjectMapping m WHERE m.employeeId = :empId")
    List<EmployeeProjectMapping> findAllByEmployeeId(@Param("empId") Long employeeId);
}
