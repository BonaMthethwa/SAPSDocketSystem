package com.docketsystem.sapsdocketsystem.Controls;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.docketsystem.sapsdocketsystem.Models.Docket;
import com.docketsystem.sapsdocketsystem.Models.Evidence;
import com.docketsystem.sapsdocketsystem.Models.User;
import com.docketsystem.sapsdocketsystem.Repositories.DocketRepository;
import com.docketsystem.sapsdocketsystem.Repositories.EvidenceRepository;
import com.docketsystem.sapsdocketsystem.Services.DocketService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class DocketController {
    @Autowired
    private DocketService docketService;
    @Autowired
    private DocketRepository docketRepository;
    @Autowired
    private EvidenceRepository evidenceRepository;

    private Docket docket;
    private static final String STORAGE_PATH = "C:/complaints/";

    
    @GetMapping("/createDocket")
    public String showDocketForm(Model model) { 
        Docket docket = new Docket();
        
        model.addAttribute("docket", docket);
        return "createDocket";
    }

    @PostMapping("/createDocket")
    public String handleComplaint(
            @RequestParam("complainantName") String name,
            @RequestParam("cellphone") String cellphone,
            @RequestParam("email") String email,
            @RequestParam("address") String address,
            @RequestParam("caseStatement") String statement,
            @RequestParam("evidence") MultipartFile[] evidence,
            @ModelAttribute Docket docket, HttpSession session, RedirectAttributes redirectAttributes) {

        
        docket.setDateCreated(LocalDateTime.now());
        docketService.saveDocket(docket); 
        docket.setCaseStatus("Open");
        docket.setIsDeleted(false);

        
        docket.setCaseNumber(docketService.generateCaseNumber());
        User currentUser = (User) session.getAttribute("loggedUser");
        if (currentUser != null) {
            docket.setCreatedBy(currentUser);
        }

        docketRepository.save(docket);
         String caseNumber = docket.getCaseNumber();
    redirectAttributes.addFlashAttribute("caseNumber", caseNumber);
        

        try {
            String folderPath = STORAGE_PATH + docket.getCaseNumber() + "/";
            File dir = new File(folderPath);
            if (!dir.exists()) dir.mkdirs();

            String pdfFilePath = folderPath + docket.getCaseNumber() + "_Complaint_Details.pdf";
            saveComplaintAsPDF(pdfFilePath, name, cellphone, email, address, statement, currentUser, docket);

            for (MultipartFile file : evidence) {
        if (!file.isEmpty()) {
        try {
            Path filePath = Path.of(folderPath + file.getOriginalFilename());
            if (Files.exists(filePath)) {
                filePath = Path.of(folderPath + "unique_" + file.getOriginalFilename());
            }
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Save Evidence entry
            Evidence evidenceEntity = new Evidence();
            evidenceEntity.setFileName(file.getOriginalFilename());
            evidenceEntity.setFilePath(filePath.toString());
            evidenceEntity.setUploadDate(new Date());
            evidenceEntity.setDocket(docket);

            evidenceRepository.save(evidenceEntity);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

            System.out.println("Complaint saved successfully!");

        }} catch (Exception e) {
            e.printStackTrace();
            return "redirect:/errorPage";
        }
        
        return "redirect:/successPage";
    }

    private void saveComplaintAsPDF(String filePath, String name, String cellphone, String email, String address, String statement, User createdBy, Docket docket) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA, 16);
                contentStream.setLeading(20);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 700);

                contentStream.showText("Complaint Details");
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                 if (createdBy != null) {
                String creatorName = createdBy.getName() + " " + createdBy.getSurname();
                contentStream.showText("Created By: " + sanitize(creatorName));
                contentStream.newLine();
            }
                contentStream.showText("Name: " + sanitize(name));
                contentStream.newLine();
                contentStream.showText("Cellphone: " + sanitize(cellphone));
                contentStream.newLine();
                contentStream.showText("Email: " + sanitize(email));
                contentStream.newLine();
                contentStream.showText("Address: " + sanitize(address));
                contentStream.newLine();
                
                
                contentStream.showText("Statement:");
                contentStream.newLine();

                

                String[] statementLines = sanitize(statement).split("(?<=\\G.{80})"); // 80-char chunks
                for (String line : statementLines) {
                    contentStream.showText(line.trim());
                    contentStream.newLine();
                }

                contentStream.endText();
            }

            document.save(filePath);
        }
    }

    private String sanitize(String input) {
        return input == null ? "" : input.replaceAll("[\\r\\n]+", " ");
    }

    @GetMapping("/successPage")
    public String goToSuccessPage(Model model) {
        model.addAttribute("docket", docket);
        return "successPage";
    }

    @GetMapping("/errorPage")
    public String goToErrorPage() {
        return "errorPage";
    }

    @GetMapping("/editDocket")
public String viewAllDockets(Model model) {
    List<Docket> allDockets = docketService.getAllDockets(); 
    model.addAttribute("dockets", allDockets);
    return "editDocket"; 
}

@PostMapping("/delete/{id}")
public String softDeleteDocket(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
    User currentUser = (User) session.getAttribute("loggedUser");
    if (currentUser == null) {
        
        return "redirect:/login";
    }
    docketService.softDeleteDocket(id, currentUser);
    redirectAttributes.addFlashAttribute("successMessage", "Docket successfully deleted.");
    return "redirect:/editDocket";
}

    @GetMapping("/edit/{caseNumber}")
public String editDocket(@PathVariable String caseNumber, Model model) {
    Optional<Docket> optionalDocket = docketRepository.findByCaseNumber(caseNumber);

    if (optionalDocket.isPresent()) {
        model.addAttribute("docket", optionalDocket.get());
        return "updateDocket"; 
    } else {
        model.addAttribute("errorMessage", "Docket not found.");
        return "redirect:/editDocket";
    }
}

public void generatePdf(Docket docket, String filePath) throws IOException {
    try (PDDocument document = new PDDocument()) {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.beginText();
        contentStream.setLeading(18f);
        contentStream.newLineAtOffset(50, 750);

        if (docket.getEditedBy() != null && !docket.getEditedBy().isEmpty()) {
                contentStream.showText("Edited By: " + sanitize(docket.getEditedBy()));
                contentStream.newLine();
            } else {
                contentStream.showText("Edited By: N/A");
                contentStream.newLine();
            }

        contentStream.showText("Case Number: " + docket.getCaseNumber());
        contentStream.newLine();
        contentStream.showText("Complainant Name: " + docket.getComplainantName());
        contentStream.newLine();
        contentStream.showText("Case Title: " + docket.getCaseTitle());
        contentStream.newLine();
        contentStream.showText("Status: " + docket.getCaseStatus());
        contentStream.newLine();
        contentStream.showText("Additional Info: " + docket.getAdditionalInfo());
        contentStream.newLine();
        contentStream.showText("Edited By: " + docket.getEditedBy());
        contentStream.endText();
        contentStream.close();

        document.save(filePath);
    }
}


@PostMapping("/updateDocket")
public String updateDocket(@ModelAttribute Docket updatedDocket, HttpSession session, RedirectAttributes redirectAttributes) {
    Optional<Docket> existingDocketOpt = docketRepository.findById(updatedDocket.getId());

    if (existingDocketOpt.isPresent()) {
        Docket existingDocket = existingDocketOpt.get();

       
        User currentUser = (User) session.getAttribute("loggedUser");
        if (currentUser != null) {
            
            existingDocket.setEditedBy(currentUser.getName() + " " + currentUser.getSurname());
        } else {
            existingDocket.setEditedBy("Unknown");
        }

        
        existingDocket.setAdditionalInfo(updatedDocket.getAdditionalInfo());

        docketRepository.save(existingDocket);

        try {
            String caseFolderPath = "C:/complaints/" + existingDocket.getCaseNumber();
            Path folderPath = Paths.get(caseFolderPath);

            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            String pdfFileName = "Docket_" + existingDocket.getCaseNumber() + ".pdf";
            Path pdfFilePath = folderPath.resolve(pdfFileName);

            generatePdf(existingDocket, pdfFilePath.toString());

            redirectAttributes.addFlashAttribute("successMessage", "Docket updated and PDF saved successfully.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Docket updated, but PDF generation failed.");
            e.printStackTrace();
        }

    } else {
        redirectAttributes.addFlashAttribute("errorMessage", "Docket not found.");
    }

    return "redirect:/editDocket";
}


@GetMapping("/dockets/search")
public String searchDockets(@RequestParam("keyword") String keyword, Model model) {
    List<Docket> dockets = docketService.searchByCaseNumber(keyword); 
    model.addAttribute("dockets", dockets);
    model.addAttribute("keyword", keyword);
    if (dockets.isEmpty()) {
        model.addAttribute("errorMessage", "No dockets found for: " + keyword);
    }
    System.out.println("Search keyword: " + keyword);
    System.out.println("Results found: " + dockets.size());

    return "welcomePage"; 
}

@GetMapping("/dockets/updateDocket/{id}")
public String showUpdateForm(@PathVariable Long id, Model model) {
    Optional<Docket> optionalDocket = docketRepository.findById(id);

    if (optionalDocket.isPresent()) {
        Docket docket = optionalDocket.get();
        model.addAttribute("docket", docket);
        return "redirect:/editDocket#docket-" + docket.getId(); 
    } else {
        return "redirect:/errorPage"; 
    }
}

@GetMapping("/dockets/download/{id}")
public void downloadDocket(@PathVariable Long id, HttpServletResponse response) throws IOException {
    Docket docket = docketRepository.findById(id).orElse(null);

    if (docket == null) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Docket not found");
        return;
    }

    
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    
    try (PDDocument document = new PDDocument()) {
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.setLeading(14.5f);
        contentStream.newLineAtOffset(50, 700);

        contentStream.showText("Case Number: " + docket.getCaseNumber());
        contentStream.newLine();
        contentStream.showText("Complainant Name: " + docket.getComplainantName());
        contentStream.newLine();
        contentStream.showText("Case Title: " + docket.getCaseTitle());
        contentStream.newLine();
        contentStream.showText("Case Statement: " + docket.getCaseStatement());
        contentStream.newLine();
        contentStream.showText("Status: " + docket.getCaseStatus());
        contentStream.newLine();
        contentStream.showText("Additional Info: " + docket.getAdditionalInfo());
        contentStream.endText();
        contentStream.close();

        document.save(out);
    }

    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=docket_" + docket.getCaseNumber() + ".pdf");
    response.getOutputStream().write(out.toByteArray());
    response.getOutputStream().flush();
}

@GetMapping("/downloadDocketPDF/{id}")
public void downloadCompleteDocket(@PathVariable Long id, HttpServletResponse response) throws IOException {
    
    Docket docket = docketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Docket not found"));

    
    Path tempDir = Files.createTempDirectory("docket_" + docket.getCaseNumber());

    
    Path pdfPath = tempDir.resolve("Docket_" + docket.getCaseNumber() + ".pdf");
    saveComplaintAsPDF(
            pdfPath.toString(),
            docket.getComplainantName(),
            docket.getCellphone(),
            docket.getEmail(),
            docket.getAddress(),
            docket.getCaseStatement(),
            docket.getCreatedBy(),
            docket
    );

    
    Path evidenceDir = Paths.get("C:/complaints/" + docket.getCaseNumber());
    if (Files.exists(evidenceDir)) {
        List<Path> evidenceFiles = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(evidenceDir)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    evidenceFiles.add(file);
                }
            }
        }

        for (Path file : evidenceFiles) {
            Files.copy(file, tempDir.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    
    Path zipPath = Files.createTempFile("docket_" + docket.getCaseNumber(), ".zip");
    ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath));

    List<Path> allFiles = new ArrayList<>();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(tempDir)) {
        for (Path file : stream) {
            if (Files.isRegularFile(file)) {
                allFiles.add(file);
            }
        }
    }

    for (Path file : allFiles) {
        ZipEntry zipEntry = new ZipEntry(file.getFileName().toString());
        zos.putNextEntry(zipEntry);
        Files.copy(file, zos);
        zos.closeEntry();
    }
    zos.close();

    
    response.setContentType("application/zip");
    response.setHeader("Content-Disposition", "attachment; filename=Docket_" + docket.getCaseNumber() + ".zip");
    Files.copy(zipPath, response.getOutputStream());
    response.getOutputStream().flush();

    
    List<Path> cleanupPaths = new ArrayList<>();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(tempDir)) {
        for (Path file : stream) {
            cleanupPaths.add(file);
        }
    }
    Collections.sort(cleanupPaths, Collections.reverseOrder());

    for (Path path : cleanupPaths) {
        Files.deleteIfExists(path);
    }
    Files.deleteIfExists(tempDir);
    Files.deleteIfExists(zipPath);
}

}

