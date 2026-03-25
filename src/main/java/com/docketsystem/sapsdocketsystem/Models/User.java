package com.docketsystem.sapsdocketsystem.Models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
   private Long persal;
   
   @Column(name = "name")
   private String name;

   @Column(name = "surname")
   private String surname;

   @Column(name= "cellphone")
   private String cellphone;

   @Column(name = "email")
   private String email;

   @Column(name = "password", length = 255)
   private String password;

   @Column(name = "role") 
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Column(name = "reset_token") 
    private String resetToken;

    @Column(name = "token_creation_time")
    private LocalDateTime tokenCreationTime;

   public User() {
    
   }

   public User(Long persal, String name, String surname, String cellphone, String email, String password) {
    this.persal=persal;
    this.name=name;
    this.surname=surname;
    this.cellphone=cellphone;
    this.email=email;
    this.password=password;
   }

   public void setPersal(Long persal){
    this.persal=persal;
   }

   public Long getPersal(){
    return persal;
   }

   public void setName(String name){
    this.name=name;
   }

   public String getName(){
    return name;
   }

   public void setSurname(String surname){
    this.surname=surname;
   }

   public String getSurname(){
    return surname;
   }

   public void setCellphone(String cellphone){
    this.cellphone=cellphone;
   }
    public String getCellphone(){
     return cellphone;
    }
   public void setEmail(String email){
    this.email=email;
   }

   public String getEmail(){
    return email;
   }

   public void setPassword(String password){
    this.password=password;
   }

   public String getPassword(){
    return password;
   }

   public void setResetToken(String resetToken) {
       this.resetToken = resetToken;
   }

    public String getResetToken() {
         return resetToken;
    }      

    public void setTokenCreationTime(LocalDateTime tokenCreationTime) {
        this.tokenCreationTime = tokenCreationTime;
    }

    public LocalDateTime getTokenCreationTime() {
        return tokenCreationTime;
    }
}
