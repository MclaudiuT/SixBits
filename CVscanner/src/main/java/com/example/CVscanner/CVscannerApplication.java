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
				You are an AI assistant named Greg that helps people sort through different CVs and then matches them 
				with the approapiate Job Description. Information will be given to you. If there is no information,
				return with a message.
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

	private final
	QuestionAnswerAdvisor questionAnswerAdvisor;

	CVscannerAssistantController(ChatClient assistant, VectorStore vectorStore) {
		this.assistant = assistant;
		this.questionAnswerAdvisor = new QuestionAnswerAdvisor(vectorStore);
	}

	@GetMapping ("/{user}/inqurie")
	String inquire(@PathVariable("user") String user, @RequestParam String question) {

		var advisor = this.advisorMap.computeIfAbsent(user, _-> PromptChatMemoryAdvisor.builder(new InMemoryChatMemory()).build());

		return this.assistant
				.prompt()
				.user(question)
				.advisors(advisor, this.questionAnswerAdvisor)
				.call()
				.content();
	}
}

interface ApplicantRepository extends ListCrudRepository<Applicant, Integer> {}

record Applicant (@Id int id, String name, String experience, String qualities) {}