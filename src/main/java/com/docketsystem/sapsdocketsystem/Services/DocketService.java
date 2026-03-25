package com.docketsystem.sapsdocketsystem.Services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.docketsystem.sapsdocketsystem.Models.Docket;
import com.docketsystem.sapsdocketsystem.Models.User;
import com.docketsystem.sapsdocketsystem.Repositories.DocketRepository;

@Service
public class DocketService {

    @Autowired
    private DocketRepository docketRepository;

   public Docket saveDocket(Docket docket) {
    if (docket.getCaseNumber() == null || docket.getCaseNumber().isEmpty()) {
        String generatedCaseNumber = generateCaseNumber();
        docket.setCaseNumber(generatedCaseNumber);
    }
    return docketRepository.save(docket);
}

    public Optional<Docket> getDocketById(Long id) {
        return docketRepository.findById(id);
    }

    //(note to self)Zama remember this returns on active dockets only
   public List<Docket> getAllDockets() {
    List<Docket> allDockets = docketRepository.findAll();
    List<Docket> activeDockets = new ArrayList<>();

    for (Docket docket : allDockets) {
        if (!docket.isDeleted()) {
            activeDockets.add(docket);
        }
    }

    return activeDockets;
}


public void deleteDocketById(Long id) {
    docketRepository.deleteById(id);
}

    // (Note to self) Zama remember this soft deletes the docket)
    public void softDeleteDocket(Long id, User user) {
    Optional<Docket> optionalDocket = docketRepository.findById(id);
    if(optionalDocket.isPresent()) {
        Docket docket = optionalDocket.get();
        docket.setIsDeleted(true);
        docket.setDeletedAt(new Date());
        docket.setDeletedBy(user);
        docketRepository.save(docket);
    }
}


public String generateCaseNumber() {
    LocalDate today = LocalDate.now();

    // (Note to self) Zama remember this gets all case numbers for today
    List<String> todayCaseNumbers = docketRepository.findTodayCaseNumbers(
        today.atStartOfDay(),
        today.plusDays(1).atStartOfDay()
    );

    int maxNumber = 0;

    for (String caseNum : todayCaseNumbers) {
        String[] parts = caseNum.split("-");
        if (parts.length >= 2) {
            try {
                int num = Integer.parseInt(parts[1]);
                if (num > maxNumber) maxNumber = num;
            } catch (NumberFormatException ignored) {}
        }
    }

    int nextNumber = maxNumber + 1;
    String numberPart = String.format("%03d", nextNumber);
    String datePart = today.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

    return "CASE-" + numberPart + "-" + datePart;
}


    public List<Docket> getDeletedDockets() {
    List<Docket> allDockets = docketRepository.findAll();
    List<Docket> deletedDockets = new ArrayList<>();
    for(Docket d : allDockets) {
        if(d.isDeleted()) {
            deletedDockets.add(d);
        }
    }
    return deletedDockets;
}

public void restoreDocket(Long id, User user) {
    Optional<Docket> optionalDocket = docketRepository.findById(id);
    if(optionalDocket.isPresent()) {
        Docket docket = optionalDocket.get();
        docket.setIsDeleted(false);
        docket.setDeletedAt(null);
        docket.setDeletedBy(null);
        docketRepository.save(docket);
    }
}

public List<Docket> searchByCaseNumber(String keyword) {
    return docketRepository.findByCaseNumberContainingIgnoreCase(keyword);
}



}
