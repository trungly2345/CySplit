package manytoone.Notifications;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import manytoone.Groups.Group;
import manytoone.Users.User;

/**
 * Entity representing a notification in the system.
 * Supports various notification types including group invitations, mentions, 
 * bill updates, role changes, and group activity.
 */
@Entity
@Table(name = "notifications")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Notification {

    /**
     * Enum defining all possible notification types in the system
     */
    public enum NotificationType {
        // Group-related notifications
        GROUP_INVITATION,           // User invited to join a group
        GROUP_INVITATION_ACCEPTED,  // Someone accepted invitation to your group
        GROUP_ROLE_CHANGED,         // Your role in a group changed
        GROUP_MEMBER_ADDED,         // New member added to group
        GROUP_MEMBER_REMOVED,       // Member removed from group
        GROUP_MEMBER_LEFT,          // Member left the group
        
        // Chat-related notifications
        GROUP_CHAT_MENTION,         // You were mentioned in group chat
        GROUP_CHAT_NEW_MESSAGE,     // New message in group (unread count)
        
        // Bill-related notifications
        BILL_CREATED,               // New bill created in group
        BILL_UPDATED,               // Bill details updated
        BILL_DELETED,               // Bill deleted
        BILL_PAYMENT_REQUIRED,      // You need to pay for a bill
        BILL_PAYMENT_RECEIVED,      // Payment received for bill
        BILL_SPLIT_UPDATED,         // Bill split ratios changed
        
        // Contribution notifications
        CONTRIBUTION_ADDED,         // Someone added contribution to group
        CONTRIBUTION_REMINDER,      // Reminder to contribute
        
        // System notifications
        SYSTEM_ANNOUNCEMENT,        // General system announcement
        ACCOUNT_UPDATE              // Account-related update
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_group_id")
    private Group relatedGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "triggered_by_user_id")
    private User triggeredBy;

    @Column(name = "related_entity_id")
    private Integer relatedEntityId; // ID of bill, invitation, etc.

    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType; // "BILL", "INVITATION", "MESSAGE", etc.

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "priority", nullable = false)
    private Integer priority = 1; // 1=low, 2=medium, 3=high, 4=urgent

    @Column(name = "action_url", length = 500)
    private String actionUrl; // Deep link or URL for action

    protected Notification() {}

    public Notification(User recipient, NotificationType type, String title, String message) {
        this.recipient = recipient;
        this.type = type;
        this.title = title;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
        this.priority = 1;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Group getRelatedGroup() {
        return relatedGroup;
    }

    public void setRelatedGroup(Group relatedGroup) {
        this.relatedGroup = relatedGroup;
    }

    public User getTriggeredBy() {
        return triggeredBy;
    }

    public void setTriggeredBy(User triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public Integer getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(Integer relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public String getRelatedEntityType() {
        return relatedEntityType;
    }

    public void setRelatedEntityType(String relatedEntityType) {
        this.relatedEntityType = relatedEntityType;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
        if (isRead && this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    /**
     * Helper method to mark notification as read
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * Helper method to check if notification has expired
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}
