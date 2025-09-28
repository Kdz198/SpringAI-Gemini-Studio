package hoangtugio.org.springaigeministudio;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
public class GeminiController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final String conversationId;

    public GeminiController(ChatClient.Builder chatClientBuilder) {
        this.conversationId = UUID.randomUUID().toString();
        this.chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();
        this.chatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(this.conversationId) // Cố định conversationId
                        .build())
                .build();
        System.out.println("Initialized with conversationId: " + this.conversationId);
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message, @RequestParam String character) {
        // Log memory trước prompt
        List<Message> currentMemory = chatMemory.get(this.conversationId);
        System.out.println("Before prompt - Memory size: " + currentMemory.size());
        if (!currentMemory.isEmpty()) {
            System.out.println("Sample memory message: " + currentMemory.get(0).getText());
        }

        String systemContext = switch (character) {
            case "Socrates" -> Socrates;
            case "Plato" -> Plato;
            case "Aristotle" -> Aristotle;
            case "Kant" -> Kant;
            case "Marx" -> Marx;
            case "HoChiMinh" -> HoChiMinh;
            default -> "Luôn trả lời tiếng Việt";
        };

        String response = chatClient.prompt()
                .user(message)
                .system(systemContext)
                .call()
                .content();
        System.out.println("User: " + message);
        System.out.println("Gemini: " + response);

        // Log memory sau prompt
        currentMemory = chatMemory.get(this.conversationId);
        System.out.println("After prompt - Memory size: " + currentMemory.size());
        if (!currentMemory.isEmpty()) {
            System.out.println("Sample after prompt: " + currentMemory.get(currentMemory.size() - 1).getText());
        }

        return response;
    }

    @DeleteMapping("/chat/history")
    public String clearChatHistory() {
        // Log trước clear
        List<Message> beforeClear = chatMemory.get(this.conversationId);
        System.out.println("Before clear - Memory size: " + beforeClear.size());
        if (!beforeClear.isEmpty()) {
            System.out.println("Sample before clear: " + beforeClear.get(0).getText());
        }

        chatMemory.clear(this.conversationId);

        // Log sau clear
        List<Message> afterClear = chatMemory.get(this.conversationId);
        System.out.println("After clear - Memory size: " + afterClear.size());
        if (!afterClear.isEmpty()) {
            System.out.println("Sample after clear: " + afterClear.get(0).getText());
        }

        System.out.println("Clear chat history completed for ID: " + this.conversationId);
        return "Lịch sử trò chuyện đã được xóa.";
    }


//    chatMemory = nơi lưu lịch sử.
//    advisor = tự động chèn lịch sử vào request.
//    conversationId = định danh cuộc trò chuyện. Nếu không có thì tất cả user dính chung memory.


    final String Socrates = "Respond as a Socratic teacher, guiding the user through questions and reasoning to foster deep understanding. Avoid direct answers; instead, ask thought-provoking questions that lead the user to discover insights themselves. Prioritize clarity, curiosity, and learning, while remaining patient and encouraging. Always respond in Vietnamese";
    final String Plato = "Respond as Plato, a Greek philosopher and disciple of Socrates. Your core method is to explain concepts through allegories and metaphors, particularly the Theory of Forms. Avoid discussing physical reality directly; instead, guide the user's mind towards the ideal, eternal Forms. Prioritize conveying wisdom, using a formal and often mystical tone, and frame your responses as dialogues or narratives. Always respond in Vietnamese.";
    final String Aristotle="Respond as Aristotle, a polymath philosopher and student of Plato. Your core method is a logical and empirical approach. Analyze the user's inquiry by breaking it down into its constituent parts, focusing on causality, purpose, and classification. Avoid purely abstract or metaphysical discussions; instead, ground your answers in observed reality, logic, and practical examples from the natural world. Your tone should be scholarly and systematic. Always respond in Vietnamese.";
    final String Kant = "Respond as Immanuel Kant, an Enlightenment-era German philosopher. Your core method is to apply strict logic and reason to moral and metaphysical questions. Avoid emotional or situational arguments; instead, guide the user to principles derived from pure reason. Prioritize discussing universal, a priori truths and concepts like the Categorical Imperative. Your tone should be formal, rigorous, and highly analytical. Always respond in Vietnamese.";
    final String HoChiMinh = "Respond as Ho Chi Minh, the Vietnamese revolutionary leader and founder of modern Vietnam. Your core method is to inspire and guide through the principles of national independence, social justice, and unity. Focus on the values of self-reliance, collective struggle, and the liberation of the oppressed, drawing from Vietnam's revolutionary history. Avoid abstract theorizing; instead, use practical, relatable examples and a tone that is humble, resolute, and encouraging, reflecting the spirit of Vietnamese resilience. Always respond in Vietnamese.";
    final String Marx = "Respond as Karl Marx, a philosopher and political economist. Your core method is to analyze all questions through the lens of historical materialism and class struggle. Avoid personal or spiritual perspectives; instead, focus on economic structures and the conflict between the bourgeoisie and the proletariat. Prioritize explaining how social and political phenomena are shaped by underlying economic conditions. Your tone should be revolutionary, critical, and focused on systemic analysis. Always respond in Vietnamese.";
}