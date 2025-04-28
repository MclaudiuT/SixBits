package com.example.CVscanner;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
/*import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;*/
import java.io.File;
import java.io.FileOutputStream;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/*import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;*/

@SpringBootApplication
@Configuration
@EnableAutoConfiguration(exclude = {
		org.springframework.ai.model.bedrock.titan.autoconfigure.BedrockTitanEmbeddingAutoConfiguration.class
})
public class CVscannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CVscannerApplication.class, args);
	}

	@Bean
	ChatClient chatClient(
			ChatClient.Builder builder,
			VectorStore vectorStore,
			CvRecordRepository cvRepo,
			JobDescriptionRepository jobRepo
	) {
		// 1) load every CV
		cvRepo.findAll().forEach(cv -> {
			var doc = Document.builder()
					.id("cv:" + cv.getId())
					.text("""
							name: %s
							email: %s
							phone: %s
							skills: %s
							experience: %s
							text: %s
							"""
							.formatted(
									cv.getCandidateName(),
									cv.getEmail(),
									cv.getPhoneNumber(),
									cv.getSkills(),
									cv.getExperience(),
									cv.getCvText()
							))
					.build();
			vectorStore.add(List.of(doc));
		});

		// 2) load every Job Description
		jobRepo.findAll().forEach(job -> {
			var doc = Document.builder()
					.id("job:" + job.getId())
					.text("""
							title: %s
							company: %s
							location: %s
							requirements: %s
							responsibilities: %s
							qualifications: %s
							description: %s
							"""
							.formatted(
									job.getJobTitle(),
									job.getCompanyName(),
									job.getLocation(),
									job.getRequirements(),
									job.getResponsibilities(),
									job.getQualifications(),
									job.getDescription()
							))
					.build();
			vectorStore.add(List.of(doc));
		});

		return builder.build();
	}
}

/*
	interface ApplicantRepository extends ListCrudRepository<Applicant, Integer> {}

record Applicant (@Id int id, String name, String experience, String qualities) {}*/
