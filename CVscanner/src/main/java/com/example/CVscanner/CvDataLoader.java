package com.example.CVscanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.time.Instant;

@Component
public class CvDataLoader implements CommandLineRunner {

    private final CvRecordRepository repo;

    @Value("${app.cv.folder}")
    private String folderPath;

    public CvDataLoader(CvRecordRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) throws Exception {
        File folder = new File(folderPath);
        if (!folder.isDirectory()) {
            System.err.println("CV folder not found: " + folderPath);
            return;
        }

        for (File file : folder.listFiles()) {
            if (!file.isFile()) continue;

            String text = ExtractorService.extractText(file);
            String name = file.getName().replaceFirst("\\.[^.]+$", "");
            // you can parse more fields here if you like
            CvRecord cv = new CvRecord(
                    name, "", "", "",
                    "", "", "",
                    text, Instant.now()
            );
            repo.save(cv);
            System.out.println("Saved CV for " + name);
        }
    }
}
