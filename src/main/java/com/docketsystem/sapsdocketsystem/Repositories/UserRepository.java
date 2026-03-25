package com.docketsystem.sapsdocketsystem.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.docketsystem.sapsdocketsystem.Models.User;





public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
    Optional<User>  findByEmail(String email);
    Optional<User>  findByResetToken(String resetToken);
}
