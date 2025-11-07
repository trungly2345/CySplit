package manytoone.Conversations;
import manytoone.Users.User;
import manytoone.Users.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


    @RestController
    @RequestMapping("/conversations")
    public class ConversationController {

        private final ConversationRepository conversationRepository;
        private final UserRepository userRepository;

        public ConversationController(ConversationRepository conversationRepository,
                                      UserRepository userRepository) {
            this.conversationRepository = conversationRepository;
            this.userRepository = userRepository;
        }


        @GetMapping
        public ResponseEntity<List<Conversation>> getAllConversations() {
            List<Conversation> conversations = conversationRepository.findAll();
            return ResponseEntity.ok(conversations);
        }

        @GetMapping("/{id}")
        public ResponseEntity<Conversation> getConversationById(@PathVariable int id) {
            return conversationRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }


        @PostMapping
        public ResponseEntity<Conversation> createConversation(@RequestBody Conversation request) {

            Conversation conversation = new Conversation();
            conversation.setName(request.getName());
            conversation.setConversationType(request.getConversationType());
            conversation.setCreatedAt(LocalDateTime.now());

            // If you have a user ID field in the request, attach the creator
            if (request.getCreatedBy() != null && request.getCreatedBy().getId() != 0) {
                User creator = userRepository.findById(request.getCreatedBy().getId());
                if (creator != null) {
                    conversation.setCreatedBy(creator);
                }
            }

            Conversation saved = conversationRepository.save(conversation);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteConversation(@PathVariable int id) {
            if (!conversationRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            conversationRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
    }

