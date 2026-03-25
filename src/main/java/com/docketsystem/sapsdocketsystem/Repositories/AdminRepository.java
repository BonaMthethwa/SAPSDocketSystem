package com.docketsystem.sapsdocketsystem.Repositories;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.docketsystem.sapsdocketsystem.Models.Admin;




public interface AdminRepository extends JpaRepository<Admin,Long>{
   Admin findByName(String name); 
   Optional<Admin>  findByEmail(String email);
    
} 
