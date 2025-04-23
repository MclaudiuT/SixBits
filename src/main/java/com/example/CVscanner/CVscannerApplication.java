package com.example.CVscanner;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.yaml.snakeyaml.tokens.Token.ID.Value;

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
				You are an AI assistant designed to evaluate and match job applicants to a given job description. 
				Your task is to identify and rank applicants based on how well they align with the job description, 
				using the following criteria in order of importance:
				
				1. Qualification Match: How closely the applicant’s qualifications, experience, and credentials align 
				   						with the requirements listed in the job description. This is the most important factor.
				
				2. Technical Skills: Whether the applicant possesses the specific technical skills mentioned in the job 
									 description (e.g., Java, SQL, etc.).
				
				3. Industry Knowledge: Any relevant domain or industry-specific experience.
				
				You should:
				
					-Match the job description only with applicants who have the specific skills and qualifications required 
					(e.g., if Java is required, only include applicants who list Java as a skill).
				
					-For each matched applicant, provide:
				
						-A short explanation of why they were selected
				
						-A list of pros and cons based on the job description and their qualifications
				
					-If no applicants match, return a message stating: “No matching applicants found based on the provided job description.”
				
				The job description and applicant information will be provided to you. Wait for that input before proceeding.
				""";
		return builder.build();
	}
}

@Controller
@ResponseBody
class CVscannerAssistantController {

	private final ChatClient assistant;

	private final Map<String, PromptChatMemoryAdvisor> advisorMap =
			new ConcurrentHashMap<>();


	CVscannerAssistantController(ChatClient assistant, VectorStore vectorStore) {
		this.assistant = assistant;
	}

	@GetMapping ("/{user}/inqurie")
	String inquire(@PathVariable("user") String user, @RequestParam String question) {

		var advisor = this.advisorMap.computeIfAbsent(user, _-> PromptChatMemoryAdvisor.builder(new InMemoryChatMemory()).build());

		return this.assistant
				.prompt()
				.user(question)
				.call()
				.content();
	}
}

interface ApplicantRepository extends ListCrudRepository<Applicant, Integer> {}

record Applicant (@Id int id, String name, String experience, String qualities) {}