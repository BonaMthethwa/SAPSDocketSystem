package com.docketsystem.sapsdocketsystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.docketsystem.sapsdocketsystem.Models.Evidence;

public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    
    
}
