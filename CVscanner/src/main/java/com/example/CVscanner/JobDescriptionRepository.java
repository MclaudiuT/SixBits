package com.example.CVscanner;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {
    // you can add custom queries here, e.g.:
    // List<JobDescription> findByJobTitleContainingIgnoreCase(String title);
}
