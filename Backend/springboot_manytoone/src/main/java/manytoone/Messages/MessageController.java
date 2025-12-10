package manytoone.Messages;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import manytoone.Conversations.Conversation;
import manytoone.Conversations.ConversationRepository;
import manytoone.Groups.Group;
import manytoone.Notifications.NotificationService;
import manytoone.Users.User;
import manytoone.Users.UserRepository;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final MessageReadRepository messageReadRepository;

    public MessageController(MessageRepository messageRepository,
                             ConversationRepository conversationRepository,
                             UserRepository userRepository,
                             NotificationService notificationService,
                             MessageReadRepository messageReadRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.messageReadRepository = messageReadRepository;
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
        
        // Handle notifications for mentions and group messages
        Optional<Conversation> conversationOpt = conversationRepository.findById(req.getConversationId());
        if (conversationOpt.isPresent()) {
            Conversation conversation = conversationOpt.get();
            User sender = userRepository.findById(req.getSenderId());
            
            if (sender != null && conversation.getGroup() != null) {
                Group group = conversation.getGroup();
                
                // Check for @mentions in the message
                detectAndNotifyMentions(saved.getContent(), sender, group);
            }
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    /**
     * Detect @username mentions in message and send notifications
     */
    private void detectAndNotifyMentions(String content, User sender, Group group) {
        // Regex to find @username patterns
        Pattern pattern = Pattern.compile("@(\\w+)");
        Matcher matcher = pattern.matcher(content);
        
        while (matcher.find()) {
            String mentionedUsername = matcher.group(1);
            User mentionedUser = userRepository.findByUserName(mentionedUsername);
            
            if (mentionedUser != null) {
                // Don't notify if user mentions themselves
                if (mentionedUser.getId() != sender.getId()) {
                    // Get message preview (first 50 chars)
                    String preview = content.length() > 50 
                        ? content.substring(0, 50) + "..." 
                        : content;
                    
                    notificationService.notifyGroupChatMention(
                        mentionedUser, 
                        group, 
                        sender, 
                        preview
                    );
                }
            }
        }
    }


    /**
     * Mark a message as read by a user
     */
    @PostMapping("/{messageId}/read")
    public ResponseEntity<String> markMessageAsRead(@PathVariable int messageId, 
                                                     @RequestParam int userId) {
        Message message = messageRepository.findById(messageId)
            .orElse(null);
        User user = userRepository.findById(userId);
        
        if (message == null || user == null) {
            return ResponseEntity.badRequest().body("Message or user not found");
        }
        
        // Don't create duplicate read records
        if (!messageReadRepository.existsByMessageAndUser(message, user)) {
            MessageRead messageRead = new MessageRead(message, user);
            messageReadRepository.save(messageRead);
        }
        
        return ResponseEntity.ok("Message marked as read");
    }
    
    /**
     * Get unread message count for a user in a conversation
     */
    @GetMapping("/conversation/{conversationId}/unread/count")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable int conversationId,
                                                   @RequestParam int userId) {
        int unreadCount = messageReadRepository.countUnreadMessagesInConversation(conversationId, userId);
        return ResponseEntity.ok(unreadCount);
    }
    
    /**
     * Notify user about unread messages in a group conversation
     * This endpoint can be called periodically or when user logs in
     */
    @PostMapping("/conversation/{conversationId}/notify-unread")
    public ResponseEntity<String> notifyUnreadMessages(@PathVariable int conversationId,
                                                        @RequestParam int userId) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);
        User user = userRepository.findById(userId);
        
        if (!conversationOpt.isPresent() || user == null) {
            return ResponseEntity.badRequest().body("Conversation or user not found");
        }
        
        Conversation conversation = conversationOpt.get();
        
        // Only send notification for group conversations
        if (conversation.getGroup() == null) {
            return ResponseEntity.ok("Not a group conversation");
        }
        
        int unreadCount = messageReadRepository.countUnreadMessagesInConversation(conversationId, userId);
        
        if (unreadCount > 0) {
            notificationService.notifyGroupChatNewMessage(user, conversation.getGroup(), unreadCount);
            return ResponseEntity.ok("Notification sent for " + unreadCount + " unread messages");
        }
        
        return ResponseEntity.ok("No unread messages");
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
