package manytoone.Notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import manytoone.Notifications.Notification;
import manytoone.Notifications.NotificationRepository;
import manytoone.Notifications.NotificationWebSocket;
import manytoone.Users.User;
import manytoone.Users.UserRepository;
import manytoone.Groups.Group;
import manytoone.Groups.GroupRepository;
import manytoone.Groups.UserGroup;
import manytoone.Groups.UserGroupRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for notification business logic and helper methods
 */
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private NotificationWebSocket notificationWebSocket;

    // ========== Helper Methods for Creating Notifications ==========

    /**
     * Notify user about group invitation
     */
    @Transactional
    public Notification notifyGroupInvitation(User recipient, Group group, User invitedBy) {
        String title = "Group Invitation";
        String message = String.format("You've been invited to join group '%s' by %s", 
            group.getGroup_name(), invitedBy.getUserName());
        
        Notification notification = new Notification(
            recipient,
            Notification.NotificationType.GROUP_INVITATION,
            title,
            message
        );
        notification.setRelatedGroup(group);
        notification.setTriggeredBy(invitedBy);
        notification.setPriority(3); // High priority
        notification.setRelatedEntityType("INVITATION");
        
        Notification saved = notificationRepository.save(notification);
        sendNotificationToUser(recipient.getId(), saved);
        
        return saved;
    }

    /**
     * Notify group admin that invitation was accepted
     */
    @Transactional
    public Notification notifyInvitationAccepted(User admin, Group group, User acceptedUser) {
        String title = "Invitation Accepted";
        String message = String.format("%s accepted the invitation to join '%s'", 
            acceptedUser.getUserName(), group.getGroup_name());
        
        Notification notification = new Notification(
            admin,
            Notification.NotificationType.GROUP_INVITATION_ACCEPTED,
            title,
            message
        );
        notification.setRelatedGroup(group);
        notification.setTriggeredBy(acceptedUser);
        
        Notification saved = notificationRepository.save(notification);
        sendNotificationToUser(admin.getId(), saved);
        
        return saved;
    }

    /**
     * Notify user about role change in group
     */
    @Transactional
    public Notification notifyRoleChanged(User recipient, Group group, UserGroup.Role newRole, User changedBy) {
        String title = "Role Changed";
        String message = String.format("Your role in '%s' has been changed to %s by %s", 
            group.getGroup_name(), newRole.name(), changedBy.getUserName());
        
        Notification notification = new Notification(
            recipient,
            Notification.NotificationType.GROUP_ROLE_CHANGED,
            title,
            message
        );
        notification.setRelatedGroup(group);
        notification.setTriggeredBy(changedBy);
        notification.setPriority(2); // Medium priority
        
        Notification saved = notificationRepository.save(notification);
        sendNotificationToUser(recipient.getId(), saved);
        
        return saved;
    }

    /**
     * Notify group members about new member
     */
    @Transactional
    public void notifyGroupMemberAdded(Group group, User newMember, User addedBy) {
        List<UserGroup> members = userGroupRepository.findByGroupId(group.getId());
        
        String title = "New Member Added";
        String message = String.format("%s added %s to the group '%s'", 
            addedBy.getUserName(), newMember.getUserName(), group.getGroup_name());
        
        for (UserGroup member : members) {
            // Don't notify the new member or the person who added them
            if (member.getUser().getId() != newMember.getId() && 
                member.getUser().getId() != addedBy.getId()) {
                
                Notification notification = new Notification(
                    member.getUser(),
                    Notification.NotificationType.GROUP_MEMBER_ADDED,
                    title,
                    message
                );
                notification.setRelatedGroup(group);
                notification.setTriggeredBy(addedBy);
                
                Notification saved = notificationRepository.save(notification);
                sendNotificationToUser(member.getUser().getId(), saved);
            }
        }
    }

    /**
     * Notify group members about removed member
     */
    @Transactional
    public void notifyGroupMemberRemoved(Group group, User removedMember, User removedBy) {
        List<UserGroup> members = userGroupRepository.findByGroupId(group.getId());
        
        String title = "Member Removed";
        String message = String.format("%s removed %s from the group '%s'", 
            removedBy.getUserName(), removedMember.getUserName(), group.getGroup_name());
        
        for (UserGroup member : members) {
            if (member.getUser().getId() != removedMember.getId() && 
                member.getUser().getId() != removedBy.getId()) {
                
                Notification notification = new Notification(
                    member.getUser(),
                    Notification.NotificationType.GROUP_MEMBER_REMOVED,
                    title,
                    message
                );
                notification.setRelatedGroup(group);
                notification.setTriggeredBy(removedBy);
                
                Notification saved = notificationRepository.save(notification);
                sendNotificationToUser(member.getUser().getId(), saved);
            }
        }
    }

    /**
     * Notify user about mention in group chat
     */
    @Transactional
    public Notification notifyGroupChatMention(User recipient, Group group, User mentionedBy, String messagePreview) {
        String title = String.format("Mentioned in %s", group.getGroup_name());
        String message = String.format("%s mentioned you in group chat: %s", 
            mentionedBy.getUserName(), messagePreview);
        
        Notification notification = new Notification(
            recipient,
            Notification.NotificationType.GROUP_CHAT_MENTION,
            title,
            message
        );
        notification.setRelatedGroup(group);
        notification.setTriggeredBy(mentionedBy);
        notification.setPriority(3); // High priority
        
        Notification saved = notificationRepository.save(notification);
        sendNotificationToUser(recipient.getId(), saved);
        
        return saved;
    }

    /**
     * Notify user about new messages in group chat (when they're offline)
     */
    @Transactional
    public Notification notifyGroupChatNewMessage(User recipient, Group group, int unreadCount) {
        String title = String.format("New messages in %s", group.getGroup_name());
        String message = String.format("You have %d unread message%s in '%s'", 
            unreadCount, unreadCount > 1 ? "s" : "", group.getGroup_name());
        
        Notification notification = new Notification(
            recipient,
            Notification.NotificationType.GROUP_CHAT_NEW_MESSAGE,
            title,
            message
        );
        notification.setRelatedGroup(group);
        notification.setPriority(1); // Low priority
        
        Notification saved = notificationRepository.save(notification);
        sendNotificationToUser(recipient.getId(), saved);
        
        return saved;
    }

    /**
     * Notify group members about new bill
     */
    @Transactional
    public void notifyBillCreated(Group group, User createdBy, String billTitle, double amount) {
        List<UserGroup> members = userGroupRepository.findByGroupId(group.getId());
        
        String title = "New Bill Created";
        String message = String.format("%s created a new bill '%s' for $%.2f in group '%s'", 
            createdBy.getUserName(), billTitle, amount, group.getGroup_name());
        
        for (UserGroup member : members) {
            if (member.getUser().getId() != createdBy.getId()) {
                Notification notification = new Notification(
                    member.getUser(),
                    Notification.NotificationType.BILL_CREATED,
                    title,
                    message
                );
                notification.setRelatedGroup(group);
                notification.setTriggeredBy(createdBy);
                notification.setPriority(2); // Medium priority
                
                Notification saved = notificationRepository.save(notification);
                sendNotificationToUser(member.getUser().getId(), saved);
            }
        }
    }

    /**
     * Notify user about bill update
     */
    @Transactional
    public Notification notifyBillUpdated(User recipient, Group group, User updatedBy, String billTitle) {
        String title = "Bill Updated";
        String message = String.format("%s updated the bill '%s' in group '%s'", 
            updatedBy.getUserName(), billTitle, group.getGroup_name());
        
        Notification notification = new Notification(
            recipient,
            Notification.NotificationType.BILL_UPDATED,
            title,
            message
        );
        notification.setRelatedGroup(group);
        notification.setTriggeredBy(updatedBy);
        
        Notification saved = notificationRepository.save(notification);
        sendNotificationToUser(recipient.getId(), saved);
        
        return saved;
    }

    /**
     * Notify user about payment required
     */
    @Transactional
    public Notification notifyBillPaymentRequired(User recipient, Group group, String billTitle, double amount) {
        String title = "Bill Due Soon";
        String message = String.format("You owe $%.2f for '%s' in group '%s'", 
            amount, billTitle, group.getGroup_name());
        
        Notification notification = new Notification(
            recipient,
            Notification.NotificationType.BILL_PAYMENT_REQUIRED,
            title,
            message
        );
        notification.setRelatedGroup(group);
        notification.setPriority(3); // High priority
        
        Notification saved = notificationRepository.save(notification);
        sendNotificationToUser(recipient.getId(), saved);
        
        return saved;
    }

    /**
     * Notify user about payment received
     */
    @Transactional
    public Notification notifyBillPaymentReceived(User recipient, Group group, User paidBy, double amount) {
        String title = "Payment Received";
        String message = String.format("%s paid $%.2f in group '%s'", 
            paidBy.getUserName(), amount, group.getGroup_name());
        
        Notification notification = new Notification(
            recipient,
            Notification.NotificationType.BILL_PAYMENT_RECEIVED,
            title,
            message
        );
        notification.setRelatedGroup(group);
        notification.setTriggeredBy(paidBy);
        
        Notification saved = notificationRepository.save(notification);
        sendNotificationToUser(recipient.getId(), saved);
        
        return saved;
    }

    /**
     * Notify user about contribution added
     */
    @Transactional
    public Notification notifyContributionAdded(User recipient, Group group, User addedBy, String contributionDetails) {
        String title = "New Contribution";
        String message = String.format("%s added a contribution to group '%s': %s", 
            addedBy.getUserName(), group.getGroup_name(), contributionDetails);
        
        Notification notification = new Notification(
            recipient,
            Notification.NotificationType.CONTRIBUTION_ADDED,
            title,
            message
        );
        notification.setRelatedGroup(group);
        notification.setTriggeredBy(addedBy);
        
        Notification saved = notificationRepository.save(notification);
        sendNotificationToUser(recipient.getId(), saved);
        
        return saved;
    }

    /**
     * Send system announcement to all users or specific group
     */
    @Transactional
    public void sendSystemAnnouncement(String title, String message, Group group) {
        List<User> recipients;
        
        if (group != null) {
            List<UserGroup> members = userGroupRepository.findByGroupId(group.getId());
            recipients = members.stream().map(UserGroup::getUser).toList();
        } else {
            recipients = userRepository.findAll();
        }
        
        for (User user : recipients) {
            Notification notification = new Notification(
                user,
                Notification.NotificationType.SYSTEM_ANNOUNCEMENT,
                title,
                message
            );
            notification.setRelatedGroup(group);
            notification.setPriority(3); // High priority
            
            Notification saved = notificationRepository.save(notification);
            sendNotificationToUser(user.getId(), saved);
        }
    }

    // ========== Cleanup Methods ==========

    /**
     * Delete old read notifications (older than specified days)
     */
    @Transactional
    public int cleanupOldNotifications(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        return notificationRepository.deleteOldReadNotifications(cutoffDate);
    }

    /**
     * Delete expired notifications
     */
    @Transactional
    public int cleanupExpiredNotifications() {
        LocalDateTime now = LocalDateTime.now();
        return notificationRepository.deleteExpiredNotifications(now);
    }

    // ========== WebSocket Communication ==========

    /**
     * Send notification to user via WebSocket
     */
    public void sendNotificationToUser(int userId, Notification notification) {
        try {
            notificationWebSocket.sendNotificationToUser(userId, notification);
        } catch (Exception e) {
            // Log error but don't fail the notification creation
            System.err.println("Failed to send WebSocket notification to user " + userId + ": " + e.getMessage());
        }
    }

    /**
     * Send notification update (read/delete) via WebSocket
     */
    public void sendNotificationUpdate(int userId, String updateType, Object data) {
        try {
            notificationWebSocket.sendUpdateToUser(userId, updateType, data);
        } catch (Exception e) {
            System.err.println("Failed to send WebSocket update to user " + userId + ": " + e.getMessage());
        }
    }

    /**
     * Broadcast notification to all users in a group
     */
    public void broadcastToGroup(int groupId, Object message) {
        try {
            notificationWebSocket.broadcastToGroup(groupId, message);
        } catch (Exception e) {
            System.err.println("Failed to broadcast to group " + groupId + ": " + e.getMessage());
        }
    }
}
