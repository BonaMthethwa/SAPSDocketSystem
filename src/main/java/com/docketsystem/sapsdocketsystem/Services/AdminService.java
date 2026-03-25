package com.docketsystem.sapsdocketsystem.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.docketsystem.sapsdocketsystem.Models.Admin;
import com.docketsystem.sapsdocketsystem.Models.Docket;
import com.docketsystem.sapsdocketsystem.Models.User;
import com.docketsystem.sapsdocketsystem.Repositories.AdminRepository;
import com.docketsystem.sapsdocketsystem.Repositories.DocketRepository;
import com.docketsystem.sapsdocketsystem.Repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocketRepository docketRepository;

    @Autowired
    private AdminRepository adminRepository;

    public boolean validateAdmin(String name, String password) {
        
        Admin admin = adminRepository.findByName(name);
        
        
        if (admin != null && admin.getPassword().equals(password)) {
            return true; 
        }
        return false; 
    }
    
    public User addUser(User user) {
        return userRepository.save(user); 
    }

    
    public List<User> getAllUsers() {
        return userRepository.findAll(); 
    }


    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id); 
    }

    
    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id).orElse(null);
        
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }
        
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        
        return userRepository.save(existingUser);
    }
    

    
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        userRepository.delete(user);  
    }
    

    
    public List<Docket> getAllDockets() {
        return docketRepository.findAll();
    }

    
    public Docket restoreDocket(Long docketId) {
        Docket docket = docketRepository.findById(docketId).orElse(null);
        
        if (docket == null) {
            throw new RuntimeException("Docket not found");
        }
    
        if (!docket.isDeleted()) {
            throw new RuntimeException("Docket is not marked as deleted");
        }
    
        
        docket.setIsDeleted(false);
        
        
        return docketRepository.save(docket);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Optional<Admin> adminOptional = adminRepository.findByEmail(email);
    
    if (!adminOptional.isPresent()) {
        throw new UsernameNotFoundException("Admin not found with email: " + email);
    }

    
    Admin admin = adminOptional.get();
System.out.println("Loaded admin email: " + admin.getEmail());
System.out.println("Loaded admin password: " + admin.getPassword());
    return org.springframework.security.core.userdetails.User.builder()
            .username(admin.getEmail())
            .password(admin.getPassword())
            .roles("ADMIN")
            .build();
    }

   
    
}
