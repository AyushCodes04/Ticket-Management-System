package com.tms.ticket_management.repository;

import com.tms.ticket_management.model.ValidationResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ValidationResultRepository extends JpaRepository<ValidationResultEntity, Long> {
    List<ValidationResultEntity> findByOrderByTimeDesc();
}
