package manytoone.Notifications;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * WebSocket server endpoint for handling real-time notifications.
 *
 * Each user connects to this server using their unique userId.
 * The server supports sending notifications, updates, and broadcasts
 * to connected users.
 *
 * Example URL: ws://localhost:8080/NotificationServer/{userId}
 */
@ServerEndpoint("/NotificationServer/{userId}")
@Component
public class NotificationWebSocket {

    // Store all active sessions mapped by userId
    private static Map<Integer, Session> userSessions = new ConcurrentHashMap<>();
    
    // JSON mapper for serialization
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Called when a new WebSocket connection is opened
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") int userId) {
        userSessions.put(userId, session);
        
        System.out.println("NotificationServer: User " + userId + " connected. Total connections: " + userSessions.size());
        
        // Send connection confirmation
        try {
            Map<String, Object> response = Map.of(
                "type", "CONNECTION_ESTABLISHED",
                "userId", userId,
                "message", "Connected to notification server",
                "timestamp", System.currentTimeMillis()
            );
            session.getBasicRemote().sendText(objectMapper.writeValueAsString(response));
        } catch (IOException e) {
            System.err.println("Error sending connection confirmation: " + e.getMessage());
        }
    }

    /**
     * Called when a message is received from client
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") int userId) {
        System.out.println("NotificationServer: Received message from user " + userId + ": " + message);
        
        try {
            // Parse message as JSON
            Map<String, Object> messageData = objectMapper.readValue(message, Map.class);
            String action = (String) messageData.get("action");
            
            switch (action) {
                case "PING":
                    // Heartbeat response
                    Map<String, Object> pong = Map.of(
                        "type", "PONG",
                        "timestamp", System.currentTimeMillis()
                    );
                    session.getBasicRemote().sendText(objectMapper.writeValueAsString(pong));
                    break;
                    
                case "GET_STATUS":
                    // Send connection status
                    Map<String, Object> status = Map.of(
                        "type", "STATUS",
                        "userId", userId,
                        "connected", true,
                        "timestamp", System.currentTimeMillis()
                    );
                    session.getBasicRemote().sendText(objectMapper.writeValueAsString(status));
                    break;
                    
                default:
                    // Unknown action
                    Map<String, Object> error = Map.of(
                        "type", "ERROR",
                        "message", "Unknown action: " + action
                    );
                    session.getBasicRemote().sendText(objectMapper.writeValueAsString(error));
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            try {
                Map<String, Object> error = Map.of(
                    "type", "ERROR",
                    "message", "Failed to process message: " + e.getMessage()
                );
                session.getBasicRemote().sendText(objectMapper.writeValueAsString(error));
            } catch (IOException ioException) {
                System.err.println("Error sending error message: " + ioException.getMessage());
            }
        }
    }

    /**
     * Called when WebSocket connection is closed
     */
    @OnClose
    public void onClose(Session session, @PathParam("userId") int userId) {
        userSessions.remove(userId);
        System.out.println("NotificationServer: User " + userId + " disconnected. Total connections: " + userSessions.size());
    }

    /**
     * Called when an error occurs
     */
    @OnError
    public void onError(Session session, @PathParam("userId") int userId, Throwable throwable) {
        System.err.println("NotificationServer: Error for user " + userId + ": " + throwable.getMessage());
        userSessions.remove(userId);
    }

    // ========== Public Methods for Sending Notifications ==========

    /**
     * Send notification to a specific user
     */
    public void sendNotificationToUser(int userId, Notification notification) {
        Session session = userSessions.get(userId);
        
        if (session != null && session.isOpen()) {
            try {
                Map<String, Object> message = Map.of(
                    "type", "NEW_NOTIFICATION",
                    "notification", convertNotificationToMap(notification),
                    "timestamp", System.currentTimeMillis()
                );
                
                session.getBasicRemote().sendText(objectMapper.writeValueAsString(message));
                System.out.println("NotificationServer: Sent notification to user " + userId);
            } catch (IOException e) {
                System.err.println("NotificationServer: Failed to send notification to user " + userId + ": " + e.getMessage());
            }
        } else {
            System.out.println("NotificationServer: User " + userId + " is not connected, notification will be stored for later");
        }
    }

    /**
     * Send update to user (e.g., notification read, deleted)
     */
    public void sendUpdateToUser(int userId, String updateType, Object data) {
        Session session = userSessions.get(userId);
        
        if (session != null && session.isOpen()) {
            try {
                Map<String, Object> message = Map.of(
                    "type", updateType,
                    "data", data != null ? data : Map.of(),
                    "timestamp", System.currentTimeMillis()
                );
                
                session.getBasicRemote().sendText(objectMapper.writeValueAsString(message));
                System.out.println("NotificationServer: Sent update '" + updateType + "' to user " + userId);
            } catch (IOException e) {
                System.err.println("NotificationServer: Failed to send update to user " + userId + ": " + e.getMessage());
            }
        }
    }

    /**
     * Broadcast message to all connected users in a group
     */
    public void broadcastToGroup(int groupId, Object message) {
        // Note: This requires tracking which users belong to which groups
        // For now, we'll implement a simple broadcast to all connected users
        // In production, you'd maintain a group membership cache
        
        System.out.println("NotificationServer: Broadcasting to group " + groupId + " (all connected users)");
        
        for (Map.Entry<Integer, Session> entry : userSessions.entrySet()) {
            Session session = entry.getValue();
            if (session.isOpen()) {
                try {
                    Map<String, Object> broadcast = Map.of(
                        "type", "GROUP_BROADCAST",
                        "groupId", groupId,
                        "message", message,
                        "timestamp", System.currentTimeMillis()
                    );
                    session.getBasicRemote().sendText(objectMapper.writeValueAsString(broadcast));
                } catch (IOException e) {
                    System.err.println("NotificationServer: Failed to broadcast to user " + entry.getKey() + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Send unread count update to user
     */
    public void sendUnreadCountUpdate(int userId, long unreadCount) {
        Session session = userSessions.get(userId);
        
        if (session != null && session.isOpen()) {
            try {
                Map<String, Object> message = Map.of(
                    "type", "UNREAD_COUNT_UPDATE",
                    "unreadCount", unreadCount,
                    "timestamp", System.currentTimeMillis()
                );
                
                session.getBasicRemote().sendText(objectMapper.writeValueAsString(message));
            } catch (IOException e) {
                System.err.println("NotificationServer: Failed to send unread count to user " + userId + ": " + e.getMessage());
            }
        }
    }

    /**
     * Get connected user count
     */
    public int getConnectedUserCount() {
        return userSessions.size();
    }

    /**
     * Check if user is connected
     */
    public boolean isUserConnected(int userId) {
        Session session = userSessions.get(userId);
        return session != null && session.isOpen();
    }

    // ========== Helper Methods ==========

    /**
     * Convert Notification entity to Map for JSON serialization
     */
    private Map<String, Object> convertNotificationToMap(Notification notification) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        
        map.put("id", notification.getId());
        map.put("type", notification.getType().toString());
        map.put("title", notification.getTitle());
        map.put("message", notification.getMessage());
        map.put("priority", notification.getPriority());
        map.put("isRead", notification.getIsRead());
        map.put("createdAt", notification.getCreatedAt().toString());
        
        if (notification.getReadAt() != null) {
            map.put("readAt", notification.getReadAt().toString());
        }
        
        if (notification.getRelatedGroup() != null) {
            Map<String, Object> groupInfo = new ConcurrentHashMap<>();
            groupInfo.put("id", notification.getRelatedGroup().getId());
            groupInfo.put("name", notification.getRelatedGroup().getGroup_name());
            map.put("relatedGroup", groupInfo);
        }
        
        if (notification.getTriggeredBy() != null) {
            Map<String, Object> userInfo = new ConcurrentHashMap<>();
            userInfo.put("id", notification.getTriggeredBy().getId());
            userInfo.put("userName", notification.getTriggeredBy().getUserName());
            map.put("triggeredBy", userInfo);
        }
        
        if (notification.getExpiresAt() != null) {
            map.put("expiresAt", notification.getExpiresAt().toString());
        }
        
        return map;
    }
}
