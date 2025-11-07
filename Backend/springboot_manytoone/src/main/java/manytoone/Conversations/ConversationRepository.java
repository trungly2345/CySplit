package manytoone.Conversations;

import manytoone.Messages.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
    List<Message> findByConversationId(int conversationId);
}
