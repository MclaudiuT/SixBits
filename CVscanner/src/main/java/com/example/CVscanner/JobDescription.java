package com.example.CVscanner;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

@Entity
@Table(name = "job_description")
public class JobDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "location")
    private String location;

    @Column(name = "job_requirements", columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "job_responsibilities", columnDefinition = "TEXT")
    private String responsibilities;

    @Column(name = "preferred_qualifications", columnDefinition = "TEXT")
    private String qualifications;

    @Column(name = "job_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at")
    private Instant createdAt;

    protected JobDescription() {
        // JPA only
    }

    public JobDescription(
            String jobTitle,
            String companyName,
            String location,
            String requirements,
            String responsibilities,
            String qualifications,
            String description,
            Instant createdAt
    ) {
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.location = location;
        this.requirements = requirements;
        this.responsibilities = responsibilities;
        this.qualifications = qualifications;
        this.description = description;
        this.createdAt = createdAt;
    }

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public String getJobTitle() {
        return jobTitle;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getRequirements() {
        return requirements;
    }
    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getResponsibilities() {
        return responsibilities;
    }
    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    public String getQualifications() {
        return qualifications;
    }
    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
