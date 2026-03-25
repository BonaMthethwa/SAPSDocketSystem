package com.docketsystem.sapsdocketsystem.Models;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "docket")
public class Docket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String caseNumber;
    private String complainantName;
    private String cellphone;
    private String email;
    private String address;
    private String additionalInfo;

    private String caseTitle;
    @Column(name = "case_statement", columnDefinition = "TEXT")
    private String caseStatement;
    private String caseStatus;

    private String editedBy;
    
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateCreated;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy;

    @OneToMany(mappedBy = "docket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evidence> evidences;
    private boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "deleted_by_user_id")
    private User deletedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;


    public Docket() {
        
    }

    public Docket(Long id,String caseNumber,String complainantName, String caseTitle, String caseStatement, String caseStatus) {
        this.id=id;
        this.caseNumber=caseNumber;
        this.complainantName=complainantName;
        this.caseTitle=caseTitle;
        this.caseStatement=caseStatement;
        this.caseStatus=caseStatus;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber=caseNumber;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseTitle(String caseTitle) {
        this.caseTitle = caseTitle;
    }

    public String getCaseTitle() {
        return caseTitle;
    }

    public void setCaseStatement(String caseStatement) {
        this.caseStatement = caseStatement;
    }

    public String getCaseStatement() {
        return caseStatement;
    }

    public void setCaseStatus(String caseStatus) {
        this.caseStatus = caseStatus;
    }

    public String getCaseStatus() {
        return caseStatus;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setComplainantName(String complainantName) {
        this.complainantName = complainantName;
    }

    public String getComplainantName() {
        return complainantName;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    
    public User getDeletedBy() {
        return deletedBy;
    }
    public void setDeletedBy(User deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }       

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(String editedBy) {
        this.editedBy = editedBy;
    }
}
