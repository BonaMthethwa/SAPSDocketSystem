package com.docketsystem.sapsdocketsystem.Controls;

import java.util.List;
import java.util.Optional;
import com.docketsystem.sapsdocketsystem.Services.AdminService;
import com.docketsystem.sapsdocketsystem.Services.DocketService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docketsystem.sapsdocketsystem.Models.Admin;
import com.docketsystem.sapsdocketsystem.Models.Docket;
import com.docketsystem.sapsdocketsystem.Models.User;
import com.docketsystem.sapsdocketsystem.Repositories.AdminRepository;
import com.docketsystem.sapsdocketsystem.Repositories.UserRepository;


@Controller

public class AdminController {
    @Autowired
    private AdminService adminService;

    
     
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

   @Autowired
   private DocketService docketService;
    
    @GetMapping("/admin/loginAdmin1")
    public String loginAdminPage(@RequestParam(name = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        return "admin/loginAdmin1";
    }

    



@PostMapping("/admin/loginAdmin1")
public String login(@RequestParam String email, @RequestParam String password, Model model) {

    Optional<Admin> adminOptional = adminRepository.findByEmail(email);
    
    
    System.out.println("Admin found for email " + email + ": " + adminOptional.isPresent());

    if (adminOptional.isPresent()) {
        Admin admin = adminOptional.get();

        
        System.out.println("Entered password: '" + password + "'");
        System.out.println("Stored password: '" + admin.getPassword() + "'");

        if (admin.getPassword().trim().equals(password.trim())) {
            return "redirect:/welcomeAdmin";
        }
    }

    model.addAttribute("error", "Invalid email or password");
    return "admin/loginAdmin1";
}


    
    
   @GetMapping("/admin/welcomeAdmin")
public String welcomePage(Model model) {
    boolean hasDeletedDockets = !docketService.getDeletedDockets().isEmpty();
    model.addAttribute("showDeletedDocketsNotification", hasDeletedDockets);
    return "admin/welcomeAdmin";
}

    @GetMapping("/admin/addNewUser")
    public String addUser() {
        return "admin/addNewUser";
    }

    @PostMapping("/admin/addNewUser")
    public String registerUser(@RequestParam Long persal,@RequestParam String name, @RequestParam String surname,@RequestParam String cellphone,
            @RequestParam String email, @RequestParam String password, Model model) {

        User user = new User(persal,name, surname,cellphone, email, password);

        adminService.addUser(user);

        return "redirect:/admin/welcomeAdmin";
    }

    @GetMapping("/admin/viewUsers")
    public String viewUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/viewUsers";
    }

    @GetMapping("/admin/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/admin/loginAdmin1";
    }   
    
    @GetMapping("/admin/viewAllDockets")
    public String viewAllDocket(Model model) {
        List<Docket> dockets = docketService.getAllDockets();
        model.addAttribute("dockets", dockets);
        
        return "admin/viewAllDockets";    
    }

    @GetMapping("/admin/adminDeletedDockets")
public String showDeletedDockets(Model model) {
    List<Docket> deletedDockets = docketService.getDeletedDockets();
    model.addAttribute("deletedDockets", deletedDockets);
    return "admin/adminDeletedDockets";
}

@PostMapping("/admin/restoreDocket/{id}")
public String restoreDocket(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
    User currentUser = (User) session.getAttribute("loggedUser");
    docketService.restoreDocket(id, currentUser);
    redirectAttributes.addFlashAttribute("successMessage", "Docket restored successfully.");
    return "redirect:/admin/adminDeletedDockets";
}

    
@GetMapping("/admin/trackDocket")
public String trackDocket(@RequestParam("id") Long id, Model model) {
    Optional<Docket> docketOptional = docketService.getDocketById(id);

    if (docketOptional.isPresent()) {
        Docket docket = docketOptional.get();
        model.addAttribute("docket", docket);
        model.addAttribute("statusOptions", List.of("Open", "Pending Investigation", "Closed"));
        return "admin/trackDocket";
    } else {
        
        model.addAttribute("error", "Docket not found with ID: " + id);
        return "admin/error"; 
    }
}

@PostMapping("/admin/trackDocket")
public String updateDocketStatus(@RequestParam("id") Long id,
                                 @RequestParam("caseStatus") String caseStatus) {
    Optional<Docket> docketOptional = docketService.getDocketById(id);

    if (docketOptional.isPresent()) {
        Docket docket = docketOptional.get();
        docket.setCaseStatus(caseStatus);
        docketService.saveDocket(docket);
    } else {
       
        return "redirect:/admin/viewAllDockets?error=notfound";
    }

    return "redirect:/admin/viewAllDockets?success=true";
}

@GetMapping("/admin/deleteDocket")
public String deleteDocket(@RequestParam("id") Long id) {
    Optional<Docket> docketOptional = docketService.getDocketById(id);
    if (docketOptional.isPresent()) {
        docketService.deleteDocketById(id);
    }
    return "redirect:/admin/viewAllDockets?deleted=true";
}
}

