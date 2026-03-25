package com.docketsystem.sapsdocketsystem.Controls;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docketsystem.sapsdocketsystem.Models.User;
import com.docketsystem.sapsdocketsystem.Repositories.UserRepository;
import com.docketsystem.sapsdocketsystem.Services.UserService;

import jakarta.servlet.http.HttpSession;









@Controller
public class UserController {
  
   @Autowired
   private UserRepository userRepository;

   @Autowired
   private PasswordEncoder passwordEncoder; 

   @Autowired
   private UserService userService;
   

    @GetMapping("/")
    public String homePage() {
        return"index";
    }

   
   

    @GetMapping("/loginUser")
    public String loginPage(@RequestParam(name = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        return"/loginUser";
    }

    @PostMapping("/loginUser")
    public String postUserLogin(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                session.setAttribute("loggedUser", user);
                return "redirect:/welcomePage";
            }
        }
        

        model.addAttribute("error", "Invalid email or password");

        return "loginUser";
    }

    @GetMapping("/welcomePage")
    public String welcomePage() {
        return"welcomePage";
    }

   

    @GetMapping("/blabla")
    public String showPage() {
        return "blabla";
    }
    

    @GetMapping("/forgotPassword")
    public String showForgotPasswordForm() {    
        return "forgotPassword";  
    }   

    @PostMapping("/forgotPassword")
public String processForgotPassword(@RequestParam("email") String email, Model model) {
    Optional<User> user = userRepository.findByEmail(email);
    
    if (user.isPresent()) {
        
        return "redirect:/resetPassword?email=" + email;
    } else {
        model.addAttribute("errorMessage", "No user found with that email address.");
        return "forgotPassword"; 
    }
}

@PostMapping("/resetPassword")
public String resetPassword(@RequestParam("email") String email,
                            @RequestParam("newPassword") String newPassword,
                            @RequestParam("confirmPassword") String confirmPassword,
                            RedirectAttributes redirectAttributes) {

    if (!newPassword.equals(confirmPassword)) {
        redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
        redirectAttributes.addFlashAttribute("email", email);
        return "redirect:/resetPassword";
    }

    Optional<User> userOptional = userService.getUserByEmail(email);

    if (userOptional.isEmpty()) {
        redirectAttributes.addFlashAttribute("errorMessage", "No user found with that email.");
        return "redirect:/forgotPassword";
    }

    User user = userOptional.get();
    user.setPassword(passwordEncoder.encode(newPassword));
    userService.saveUser(user);

    redirectAttributes.addFlashAttribute("successMessage", "Password reset successfully. Please log in.");
    return "redirect:/loginUser";
}


    @GetMapping("/resetPassword")
public String showResetPasswordPage(@RequestParam("email") String email, Model model) {
    model.addAttribute("email", email);
    return "resetPassword"; 
}

@GetMapping("/profile")
public String profilePage() {
    return "profile"; 
}

 @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to change your password.");
            return "redirect:/loginUser";
        }

        try {
            userService.changePassword(loggedUser.getPersal(), currentPassword, newPassword, confirmPassword);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/profile";
    }



    @GetMapping("/logout")
    public String logout(HttpSession session) { 
        session.invalidate(); 
        return "redirect:/loginUser";          

    }
    
}
   
    

