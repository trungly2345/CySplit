package manytoone.Messages;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import manytoone.Users.User;

@Repository
public interface MessageReadRepository extends JpaRepository<MessageRead, Integer> {

    /**
     * Check if a user has read a specific message
     */
    boolean existsByMessageAndUser(Message message, User user);

    /**
     * Count unread messages for a user in a conversation
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversationId = :conversationId " +
           "AND m.senderId != :userId " +
           "AND NOT EXISTS (SELECT mr FROM MessageRead mr WHERE mr.message = m AND mr.user.id = :userId)")
    int countUnreadMessagesInConversation(@Param("conversationId") int conversationId, 
                                          @Param("userId") int userId);
}
