package com.docketsystem.sapsdocketsystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.docketsystem.sapsdocketsystem.Models.DocketStore;

public interface DocketStoreRepository extends JpaRepository<DocketStore, Long> {
    
}
