package manytoone.Notifications;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@ServerEndpoint("/NotificationServer/{userId}")
@Component
public class NotificationWebSocket {

    private static Map<Integer, Session> userSessions = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // WebSocket connection opened
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") int userId) {
        userSessions.put(userId, session);
        System.out.println("User " + userId + " connected to notifications");
    }

    // WebSocket connection closed
    @OnClose
    public void onClose(Session session, @PathParam("userId") int userId) {
        userSessions.remove(userId);
        System.out.println("User " + userId + " disconnected from notifications");
    }

    // WebSocket error handler
    @OnError
    public void onError(Session session, @PathParam("userId") int userId, Throwable throwable) {
        System.err.println("WebSocket error for user " + userId + ": " + throwable.getMessage());
        userSessions.remove(userId);
    }

    // Send notification to specific user via WebSocket
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
                System.out.println("Sent notification to user " + userId);
            } catch (IOException e) {
                System.err.println("Failed to send notification to user " + userId + ": " + e.getMessage());
            }
        }
    }

    // Convert Notification entity to JSON-friendly Map
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
