package manytoone.Notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import manytoone.Users.User;
import manytoone.Users.UserRepository;
import manytoone.Groups.Group;
import manytoone.Groups.GroupRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for managing notifications
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private NotificationService notificationService;

    /**
     * Get all notifications for a user
     */
    @GetMapping("/user/{userId}")
    @Transactional
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable int userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        
        List<Notification> notifications = notificationRepository.findByRecipientId(userId);
        
        // Force initialization of lazy-loaded entities
        notifications.forEach(n -> {
            if (n.getRecipient() != null) n.getRecipient().getUserName();
            if (n.getRelatedGroup() != null) n.getRelatedGroup().getId();
            if (n.getTriggeredBy() != null) n.getTriggeredBy().getUserName();
        });
        
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread notifications for a user
     */
    @GetMapping("/user/{userId}/unread")
    @Transactional
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable int userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        
        List<Notification> notifications = notificationRepository.findUnreadByUserId(userId);
        
        // Force initialization
        notifications.forEach(n -> {
            if (n.getRecipient() != null) n.getRecipient().getUserName();
            if (n.getRelatedGroup() != null) n.getRelatedGroup().getId();
            if (n.getTriggeredBy() != null) n.getTriggeredBy().getUserName();
        });
        
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread count for a user
     */
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@PathVariable int userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        
        long count = notificationRepository.countUnreadByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("unreadCount", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get notifications for a specific group
     */
    @GetMapping("/user/{userId}/group/{groupId}")
    @Transactional
    public ResponseEntity<List<Notification>> getGroupNotifications(
            @PathVariable int userId,
            @PathVariable int groupId) {
        
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<Notification> notifications = notificationRepository.findByUserIdAndGroupId(userId, groupId);
        
        // Force initialization
        notifications.forEach(n -> {
            if (n.getRecipient() != null) n.getRecipient().getUserName();
            if (n.getRelatedGroup() != null) n.getRelatedGroup().getId();
            if (n.getTriggeredBy() != null) n.getTriggeredBy().getUserName();
        });
        
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread count per group
     */
    @GetMapping("/user/{userId}/group/{groupId}/unread/count")
    public ResponseEntity<Map<String, Object>> getGroupUnreadCount(
            @PathVariable int userId,
            @PathVariable int groupId) {
        
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        
        long count = notificationRepository.countUnreadByUserIdAndGroupId(userId, groupId);
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("groupId", groupId);
        response.put("unreadCount", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get recent notifications (last 30 days)
     */
    @GetMapping("/user/{userId}/recent")
    @Transactional
    public ResponseEntity<List<Notification>> getRecentNotifications(@PathVariable int userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Notification> notifications = notificationRepository.findRecentByUserId(userId, thirtyDaysAgo);
        
        // Force initialization
        notifications.forEach(n -> {
            if (n.getRecipient() != null) n.getRecipient().getUserName();
            if (n.getRelatedGroup() != null) n.getRelatedGroup().getId();
            if (n.getTriggeredBy() != null) n.getTriggeredBy().getUserName();
        });
        
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get high priority unread notifications
     */
    @GetMapping("/user/{userId}/priority")
    @Transactional
    public ResponseEntity<List<Notification>> getHighPriorityNotifications(@PathVariable int userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        
        List<Notification> notifications = notificationRepository.findHighPriorityUnread(userId);
        
        // Force initialization
        notifications.forEach(n -> {
            if (n.getRecipient() != null) n.getRecipient().getUserName();
            if (n.getRelatedGroup() != null) n.getRelatedGroup().getId();
            if (n.getTriggeredBy() != null) n.getTriggeredBy().getUserName();
        });
        
        return ResponseEntity.ok(notifications);
    }

    /**
     * Mark a notification as read
     */
    @PutMapping("/{notificationId}/read")
    @Transactional
    public ResponseEntity<Notification> markAsRead(@PathVariable int notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        
        if (notification == null) {
            return ResponseEntity.notFound().build();
        }
        
        notification.markAsRead();
        Notification saved = notificationRepository.save(notification);
        
        // Force initialization
        if (saved.getRecipient() != null) saved.getRecipient().getUserName();
        if (saved.getRelatedGroup() != null) saved.getRelatedGroup().getId();
        if (saved.getTriggeredBy() != null) saved.getTriggeredBy().getUserName();
        
        return ResponseEntity.ok(saved);
    }

    /**
     * Mark all notifications as read for a user
     */
    @PutMapping("/user/{userId}/read-all")
    @Transactional
    public ResponseEntity<Map<String, Object>> markAllAsRead(@PathVariable int userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        
        notificationRepository.markAllAsReadForUser(userId, LocalDateTime.now());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "All notifications marked as read");
        response.put("userId", userId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Mark all group notifications as read
     */
    @PutMapping("/user/{userId}/group/{groupId}/read-all")
    @Transactional
    public ResponseEntity<Map<String, Object>> markGroupNotificationsAsRead(
            @PathVariable int userId,
            @PathVariable int groupId) {
        
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        
        notificationRepository.markAllAsReadForGroup(userId, groupId, LocalDateTime.now());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "All group notifications marked as read");
        response.put("userId", userId);
        response.put("groupId", groupId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a notification
     */
    @DeleteMapping("/{notificationId}")
    @Transactional
    public ResponseEntity<Void> deleteNotification(@PathVariable int notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        
        if (notification == null) {
            return ResponseEntity.notFound().build();
        }
        
        notificationRepository.deleteById(notificationId);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Get notification summary by type
     */
    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<Map<String, Object>> getNotificationSummary(@PathVariable int userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        
        List<Object[]> summary = notificationRepository.getUnreadSummaryByType(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        
        Map<String, Long> typeCounts = new HashMap<>();
        summary.forEach(row -> {
            Notification.NotificationType type = (Notification.NotificationType) row[0];
            Long count = (Long) row[1];
            typeCounts.put(type.name(), count);
        });
        
        response.put("unreadByType", typeCounts);
        response.put("totalUnread", notificationRepository.countUnreadByUserId(userId));
        
        return ResponseEntity.ok(response);
    }

    /**
     * Create a test notification (for testing purposes)
     */
    @PostMapping("/test")
    @Transactional
    public ResponseEntity<Notification> createTestNotification(
            @RequestParam int userId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam(required = false) Integer groupId) {
        
        User user = userRepository.findById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        Notification notification = new Notification(
            user,
            Notification.NotificationType.SYSTEM_ANNOUNCEMENT,
            title,
            message
        );
        
        if (groupId != null) {
            Group group = groupRepository.findById(groupId).orElse(null);
            if (group != null) {
                notification.setRelatedGroup(group);
            }
        }
        
        Notification saved = notificationRepository.save(notification);
        
        // Send via WebSocket
        notificationService.sendNotificationToUser(userId, saved);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
