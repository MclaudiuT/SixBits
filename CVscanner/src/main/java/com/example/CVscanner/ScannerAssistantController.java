package com.example.CVscanner;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@ResponseBody
public class ScannerAssistantController {

	private final ChatClient assistant;

	private final Map<String, PromptChatMemoryAdvisor> advisorMap =
			new ConcurrentHashMap<>();
	private final CvRecordRepository cvRecordRepository;

	//private final
	//QuestionAnswerAdvisor questionAnswerAdvisor;

	public ScannerAssistantController(
			ChatClient assistant,
			CvRecordRepository cvRecordRepository
	) {
		this.assistant = assistant;
		this.cvRecordRepository = cvRecordRepository;  // Assign the repository
	}

	@GetMapping("/{user}/inquire")
	String inquire(@PathVariable("user") String user, @RequestParam String question) {

		var advisor = this.advisorMap.computeIfAbsent(user, __-> PromptChatMemoryAdvisor.builder(new InMemoryChatMemory()).build());

		return this.assistant
				.prompt()
				.user(question)
				//.advisors(advisor, this.questionAnswerAdvisor)
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

			String candidate_name  = "Unknown Candidate";
			String email          = "unknown@example.com";
			String phone_number    = "0000000000";
			String location       = "Unknown Location";
			String skills         = "Not extracted";
			String education      = "Not extracted";
			String experience     = "Not extracted";
			String cv_text  = extractedText.length() > 500
					? extractedText.substring(0, 500) + "…"
					: extractedText;
			String created_at = Instant.now().toString();
			CvRecord cvRecord = new CvRecord(
				candidate_name,
				email,
				phone_number,
				location,
				skills,
				education,
				experience,
				cv_text,
				Instant.now()
			);
			cvRecordRepository.save(cvRecord);

			// 4) Build the enhanced prompt
			String prompt = """
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
			String aiResponse = assistant
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
