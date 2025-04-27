package com.example.CVscanner;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

/*import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;*/

import java.time.Instant;

@Entity
@Table(name = "cv")    // or whatever your table is called
public class CvRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_name")
    private String candidateName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "location")
    private String location;

    @Column(name = "skills")
    private String skills;

    @Column(name = "education")
    private String education;

    @Column(name = "experience")
    private String experience;

    @Column(name = "cv_text", columnDefinition = "TEXT")
    private String cvText;

    @Column(name = "created_at")
    private Instant createdAt;


    // constructors, getters & setters
    public CvRecord() {}

    public CvRecord(String candidateName, String email, String phoneNumber,
                    String location, String skills, String education,
                    String experience, String cvText, Instant createdAt) {
        this.candidateName = candidateName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.skills = skills;
        this.education = education;
        this.experience = experience;
        this.cvText = cvText;
        this.createdAt = createdAt;
    }

/*    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }*/

    // getters & setters...
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getCvText() {
        return cvText;
    }

    public void setCvText(String cvText) {
        this.cvText = cvText;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}



