package com.example.CVscanner;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ai.somepackage.QuestionAnswerAdvisor;


import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class CVscannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CVscannerApplication.class, args);
	}

	@Bean
	ChatClient chatClient (ChatClient.Builder builder,
						   ApplicantRepository applicantRepository,
						   VectorStore vectorStore) {

		applicantRepository.findAll().forEach(applicant -> {
			var document = new Document("id: %s, name: %s, experience: %s, qualities: %s".formatted(
					applicant.id(), applicant.name(), applicant.experience(), applicant.qualities()
			));
			vectorStore.add(List.of(document));
		});

		var system = """
				You are an AI assistant named Greg that helps people sort through different CVs and then matches them 
				with the appropriate Job Description. Information will be given to you. If there is no information,
				return with a message.
				""";
		return builder.build();
	}
}

@Controller
@ResponseBody
class ScannerAssistantController {

	private final ChatClient assistant;

	private final Map<String, PromptChatMemoryAdvisor> advisorMap =
			new ConcurrentHashMap<>();

	private final
	QuestionAnswerAdvisor questionAnswerAdvisor;

	 ScannerAssistantController(ChatClient assistant, VectorStore vectorStore) {
		this.assistant = assistant;
		this.questionAnswerAdvisor = new QuestionAnswerAdvisor(vectorStore);
	}

	@GetMapping ("/{user}/inquire")
	String inquire(@PathVariable("user") String user, @RequestParam String question) {

		var advisor = this.advisorMap.computeIfAbsent(user, unused-> PromptChatMemoryAdvisor.builder(new InMemoryChatMemory()).build());

		return this.assistant
				.prompt()
				.user(question)
				.advisors(advisor, this.questionAnswerAdvisor)
				.call()
				.content();
	}
	@PostMapping("/upload-cv")
	public String uploadCv(@RequestParam("file") MultipartFile file) {
		try {
			// Save the uploaded file temporarily
			File tempFile = File.createTempFile("uploaded-", file.getOriginalFilename());
			try (FileOutputStream fos = new FileOutputStream(tempFile)) {
				fos.write(file.getBytes());
			}

			// Use FileTextExtractorService to extract text
			String extractedText = ExtractorService.extractText(tempFile);
			String prompt= """
					You are Greg, an AI assistant that matches job applicants to job descriptions.
					            Below is a complete candidate profile. Based on their background, recommend the top 3 positions
					            from our job database.
					     
					            Candidate Profile:
					            • Name: %s
					            • Email: %s
					            • Phone: %s
					            • Location: %s
					            • Skills: %s
					            • Education: %s
					            • Experience: %s
					            • CV Text Excerpt: %s
					            • Applied At: %s
					     For each recommended job, please provide:
					                   1. Job Title & Company
					                   2. A brief rationale (1–2 sentences)
					                   3. A match score (0–100)
					     
					                 If no suitable match is found, simply respond: “No suitable match found.”
					                 """.formatted(
					                     candidate_name,
					                     email,
					                     phone_number,
					                     location,
					                     skills,
					                     education,
					                     experience,
					                     cv_text,
					                     created_at
					                 );


			// Ask AI assistant to match this CV to jobs
			String aiResponse = this.assistant
					.prompt()
					.user(prompt)
					.call()
					.content();

			// Delete temp file
			tempFile.delete();

			return aiResponse;

		} catch (Exception e) {
			throw new RuntimeException("Failed to process uploaded CV", e);
		}
	}
}

interface ApplicantRepository extends ListCrudRepository<Applicant, Integer> {}

record Applicant (@Id int id, String name, String experience, String qualities) {}