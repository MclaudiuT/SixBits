package com.example.CVscanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.time.Instant;

@Component
public class CvDataLoader implements CommandLineRunner {

    private final CvRecordRepository cvRepo;
    private final JobDescriptionRepository jobRepo;

    @Value("${app.cv.folder}")
    private String cvFolderPath;
    @Value("${app.job.folder}")
    private String jobFolderPath;

    public CvDataLoader(CvRecordRepository cvRepo, JobDescriptionRepository jobRepo) {
        this.cvRepo = cvRepo;
        this.jobRepo = jobRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1) Load CVs
        File cvDir = new File(cvFolderPath);
        if (cvDir.isDirectory()) {
            for (File file : cvDir.listFiles()) {
                if (!file.isFile()) continue;
                String text = ExtractorService.extractText(file);
                String name = stripExtension(file.getName());
                CvRecord cv = new CvRecord(
                        name,                     // candidateName
                        "",                       // email (extract later)
                        "",                       // phoneNumber
                        "",                       // location
                        "",                       // skills
                        "",                       // education
                        "",                       // experience
                        text,                     // full CV text
                        Instant.now()             // createdAt
                );
                cvRepo.save(cv);
                System.out.println("Saved CV: " + name);
            }
        } else {
            System.err.println("CV folder not found: " + cvFolderPath);
        }

        // 2) Load Job Descriptions
        File jobDir = new File(jobFolderPath);
        if (jobDir.isDirectory()) {
            for (File file : jobDir.listFiles()) {
                if (!file.isFile()) continue;
                String text = ExtractorService.extractText(file);
                String title = stripExtension(file.getName());
                JobDescription job = new JobDescription(
                        title,                   // jobTitle
                        "",                      // companyName (fill in later)
                        "",                      // location
                        text,                    // requirements (or whole blob)
                        text,                    // responsibilities
                        text,                    // preferredQualifications
                        text,                    // description
                        Instant.now()            // createdAt
                );
                jobRepo.save(job);
                System.out.println("Saved Job Description: " + title);
            }
        } else {
            System.err.println("Job folder not found: " + jobFolderPath);
        }
    }

    private String stripExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return (idx > 0 ? filename.substring(0, idx) : filename);
    }
}