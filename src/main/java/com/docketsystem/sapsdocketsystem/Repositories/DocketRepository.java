package com.docketsystem.sapsdocketsystem.Repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.docketsystem.sapsdocketsystem.Models.Docket;

public interface DocketRepository extends JpaRepository<Docket,Long> {

     @Query("SELECT d FROM Docket d WHERE d.isDeleted = false")
    List<Docket> findAllActive();

    
    @Query("SELECT d FROM Docket d WHERE d.isDeleted = true")
    List<Docket> findAllDeleted();

    
   Optional<Docket> findByCaseNumber(String caseNumber);

   

   List<Docket> findByCaseNumberContainingIgnoreCase(String caseNumber);
   int countByDateCreatedBetween(LocalDateTime start, LocalDateTime end);
   
    List<Docket> findByComplainantNameContainingIgnoreCase(String complainantName);

    
    List<Docket> findByCaseStatus(String caseStatus);

    @Query("SELECT d.caseNumber FROM Docket d WHERE d.dateCreated BETWEEN :start AND :end ORDER BY d.id DESC")
    List<String> findTodayCaseNumbers(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

  
} 
