package manytoone.websocket;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import manytoone.Groups.Group;
import manytoone.Groups.GroupInvitationRepository;
import manytoone.Groups.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Represents a WebSocket chat server for handling real-time communication
 * between users. Each user connects to the server using their unique
 * username.
 *
 * This class is annotated with Spring's `@ServerEndpoint` and `@Component`
 * annotations, making it a WebSocket endpoint that can handle WebSocket
 * connections at the "/chat/{username}" endpoint.
 *
 * Example URL: ws://localhost:8080/chat/username
 *
 * The server provides functionality for broadcasting messages to all connected
 * users and sending messages to specific users.
 */
@ServerEndpoint("/GroupServer/{group_name}/{user_name}")
@Component
public class GroupServer {

    // Store all socket session and their corresponding username
    // Two maps for the ease of retrieval by key
    private static final Map <String , Set<Session>> groupSession = new ConcurrentHashMap<>();
    private static final Map <Session, String> sessionToGroup = new ConcurrentHashMap<>();
    private static final Map < Session , String> sessionToUser = new ConcurrentHashMap<>();
    private static final Map < String, Session > usernameSessionMap = new ConcurrentHashMap <> ();
    private static final Map < String, Session > groupnameSessionMap = new ConcurrentHashMap <> ();


    // server side logger
    private final Logger logger = LoggerFactory.getLogger(GroupServer.class);

    /**
     * This method is called when a new WebSocket connection is established.
     *
     * @param session represents the WebSocket session for the connected user.
     * @param group_name username specified in path parameter.
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("group_name") String group_name, @PathParam("user_name") String username) throws IOException {

        GroupRepository groupRepository;

        groupSession.computeIfAbsent(group_name, g -> ConcurrentHashMap.newKeySet()).add(session);
        sessionToGroup.put(session, group_name);
        sessionToUser.put(session, username);
        usernameSessionMap.put(username,session);

        broadcast(group_name, "User " + username + " has joined the chat!" );

       // check if the group name duplicate exists
        if (groupnameSessionMap.containsKey(group_name)){
            session.getBasicRemote().sendText("Group Name exists!");
        }


    }

    @OnClose
    public void onClose(Session session) {
        String group = sessionToGroup.remove(session);
        String user  = sessionToUser.remove(session);
        if (group != null) {
            Set<Session> set = groupSession.get(group);
            if (set != null) set.remove(session);
            broadcast(group, "[leave] " + (user != null ? user : "user") + " left");
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException{
        // get the user from the session
        String user = sessionToUser.get(session);
        String group = sessionToGroup.get(session);

        logger.info("[onMessage] " + group + user + ": " + message);

        message = (message == null) ? " " : message.trim();

        if(message.isEmpty()){
            return;
        }

        if(message.startsWith("@")){
            String[] split_msg = message.split("\\s+");

            StringBuilder msgBuilder = new StringBuilder();
            for(int i = 1; i < split_msg.length; i++){
                msgBuilder.append(split_msg[i]).append(" ");
            }
            String toUserName = split_msg[0].substring(1);
            String Msg = msgBuilder.toString();
            sendMessageToPArticularUser(toUserName, "<Whisper from..." + user + "> : " + Msg);
            sendMessageToPArticularUser(user, "<Whisper from..." + user + "> : " + Msg);

        } else {
            broadcast(group, " <" + user + "> " + " " + message);
        }

    }

    /**
     * Sends a message to a specific user in the chat (DM).
     *
     * @param username The username of the recipient.
     * @param message  The message to be sent.
     */
    private void sendMessageToPArticularUser(String username, String message) {
        try {
            usernameSessionMap.get(username).getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.info("[DM Exception] " + e.getMessage());
        }
    }


    /**
     * Broadcasts a message to all users in the chat.
     *
     * @param message The message to be broadcasted to all users.
     */
    private void broadcast(String group_name, String message) {
        Set <Session> sessions = groupSession.get(group_name);
        sessions.forEach((session) -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                logger.info("[Broadcast Exception] " + e.getMessage());
            }
        });
    }


}

