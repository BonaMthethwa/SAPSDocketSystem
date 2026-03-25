package com.docketsystem.sapsdocketsystem.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin")
public class Admin {
   @Id
   private Long persal;
   
   @Column(name = "name")
   private String name;

   @Column(name = "surname")
   private String surname;

   @Column(name = "email", unique = true)
   private String email;

   @Column(name = "password", length = 8)
   private String password;

   public Admin() {
    
   }

   public Admin(Long persal, String name, String surname, String email, String password) {
    this.persal=persal;
    this.name=name;
    this.surname=surname;
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

}
