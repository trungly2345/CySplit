package manytoone.Messages;

import manytoone.Conversations.ConversationRepository;
import manytoone.Users.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import manytoone.Messages.Message;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public MessageController(MessageRepository messageRepository,
                             ConversationRepository conversationRepository,
                             UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        return ResponseEntity.ok(messages);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable int id) {
        return messageRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<Message>> getMessagesByConversation(@PathVariable int conversationId) {
        List<Message> messages = messageRepository.findByConversationId(conversationId);
        return ResponseEntity.ok(messages);
    }


    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody Message req) {

        // Optional: Validate that the conversation and user actually exist
        boolean convoExists = conversationRepository.existsById(req.getConversationId());
        boolean senderExists = userRepository.existsById(req.getSenderId());

        if (!convoExists || !senderExists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Message msg = new Message();
        msg.setConversationId(req.getConversationId());
        msg.setSenderId(req.getSenderId());
        msg.setContent(req.getContent());
        msg.setSentAt(LocalDateTime.now());

        Message saved = messageRepository.save(msg);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable int id) {
        if (!messageRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        messageRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
