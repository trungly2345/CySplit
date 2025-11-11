package manytoone.Notifications;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Notification entity operations
 */
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    // Find all notifications for a specific user
    List<Notification> findByRecipientId(int userId);

    // Find unread notifications for a user
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :userId AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByUserId(@Param("userId") int userId);

    // Find notifications for a user by type
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :userId AND n.type = :type ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndType(@Param("userId") int userId, @Param("type") Notification.NotificationType type);

    // Find recent notifications (last 30 days)
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :userId AND n.createdAt >= :since ORDER BY n.createdAt DESC")
    List<Notification> findRecentByUserId(@Param("userId") int userId, @Param("since") LocalDateTime since);

    // Find notifications related to a specific group
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :userId AND n.relatedGroup.id = :groupId ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndGroupId(@Param("userId") int userId, @Param("groupId") int groupId);

    // Count unread notifications for a user
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient.id = :userId AND n.isRead = false")
    long countUnreadByUserId(@Param("userId") int userId);

    // Count unread notifications by group
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient.id = :userId AND n.relatedGroup.id = :groupId AND n.isRead = false")
    long countUnreadByUserIdAndGroupId(@Param("userId") int userId, @Param("groupId") int groupId);

    // Find high priority unread notifications
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :userId AND n.isRead = false AND n.priority >= 3 ORDER BY n.priority DESC, n.createdAt DESC")
    List<Notification> findHighPriorityUnread(@Param("userId") int userId);

    // Mark notification as read
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.id = :id")
    void markAsRead(@Param("id") int id, @Param("readAt") LocalDateTime readAt);

    // Mark all notifications as read for a user
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.recipient.id = :userId AND n.isRead = false")
    void markAllAsReadForUser(@Param("userId") int userId, @Param("readAt") LocalDateTime readAt);

    // Mark all notifications for a group as read
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.recipient.id = :userId AND n.relatedGroup.id = :groupId AND n.isRead = false")
    void markAllAsReadForGroup(@Param("userId") int userId, @Param("groupId") int groupId, @Param("readAt") LocalDateTime readAt);

    // Delete read notifications older than specified date
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.isRead = true AND n.createdAt < :before")
    void deleteOldReadNotifications(@Param("before") LocalDateTime before);

    // Delete expired notifications
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.expiresAt IS NOT NULL AND n.expiresAt < :now")
    void deleteExpiredNotifications(@Param("now") LocalDateTime now);

    // Find notifications by related entity
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :userId AND n.relatedEntityType = :entityType AND n.relatedEntityId = :entityId")
    List<Notification> findByRelatedEntity(@Param("userId") int userId, @Param("entityType") String entityType, @Param("entityId") int entityId);

    // Get notification summary (count by type)
    @Query("SELECT n.type, COUNT(n) FROM Notification n WHERE n.recipient.id = :userId AND n.isRead = false GROUP BY n.type")
    List<Object[]> getUnreadSummaryByType(@Param("userId") int userId);
}
