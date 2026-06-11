package com.cld.finding.repository;

import com.cld.finding.model.SecurityFinding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FindingRepository extends JpaRepository<SecurityFinding, Long> {
}