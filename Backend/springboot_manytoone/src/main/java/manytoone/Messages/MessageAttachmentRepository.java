package manytoone.Messages;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageAttachmentRepository extends JpaRepository<MessageAttachment, Integer> {
        List<MessageAttachment> findByMessageId(int messageId);
}
