package manytoone.websocket;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@ServerEndpoint("/GroupServer/{group_name}")
@Component
public class GroupServer {

    // Store all socket session and their corresponding username
    // Two maps for the ease of retrieval by key
    private static Map < Session, String > sessionGroupNameMap = new Hashtable < > ();
    private static Map < String, Session > groupNameSessionMap = new Hashtable < > ();

    // server side logger
    private final Logger logger = LoggerFactory.getLogger(GroupServer.class);

    /**
     * This method is called when a new WebSocket connection is established.
     *
     * @param session represents the WebSocket session for the connected user.
     * @param group_name username specified in path parameter.
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("group_name") String group_name) throws IOException {

        // server side log
        logger.info("[onOpen] " + group_name);

        // Handle the case of a duplicate groupname
        if (groupNameSessionMap.containsKey(group_name)) {
            session.getBasicRemote().sendText("group name already exists");
            session.close();
        }
        else {
//            // map current session with username
//            sessionUsernameMap.put(session, username);
            sessionGroupNameMap.put(session, group_name);

            // map current username with session
            groupNameSessionMap.put(group_name, session);

//            // send to the user joining in
//            sendMessageToPArticularUser(username, "Welcome to the chat server, "+username);

            // send to everyone in the chat
           broadcast("Group: " + group_name + " has been created!");
        }
    }

    /**
     * Broadcasts a message to all users in the chat.
     *
     * @param message The message to be broadcasted to all users.
     */
    private void broadcast(String message) {
        sessionGroupNameMap.forEach((session, group_name) -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                logger.info("[Broadcast Exception] " + e.getMessage());
            }
        });
    }


}

