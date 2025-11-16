package manytoone.Messages;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message-attachments")
public class MessageAttachmentController {

    private final MessageAttachmentRepository attachmentRepo;
    private final MessageRepository messageRepo;

    public MessageAttachmentController(MessageAttachmentRepository attachmentRepo,
                                       MessageRepository messageRepo) {
        this.attachmentRepo = attachmentRepo;
        this.messageRepo = messageRepo;
    }

    @PostMapping
    public ResponseEntity<MessageAttachment> add(@RequestBody MessageAttachment req) {
        if (!messageRepo.existsById(req.getMessageId())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity
                .status(201)
                .body(attachmentRepo.save(req));
    }

    @GetMapping("/message/{messageId}")
    public ResponseEntity<List<MessageAttachment>> getForMessage(@PathVariable int messageId) {
        return ResponseEntity.ok(attachmentRepo.findByMessageId(messageId));
    }
}