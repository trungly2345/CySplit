package manytoone.Messages;

import jakarta.persistence.*;
import manytoone.Conversations.Conversation;
import manytoone.Users.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "messageId" , nullable = false)
    private int message_id; // Primary key

    @Column(name = "conversation_id" , nullable = false)
    private int conversationId;


    @Column(name = "sender_id" , nullable = false)
    private int senderId;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "sent_At", nullable = false)
    private LocalDateTime sentAt;

    protected Message(){}

    public Message(int conversation_id,int senderId, String content){
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.sentAt = LocalDateTime.now();
    }


    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversation_id) {
        this.conversationId = conversation_id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int sender_id) {
        this.senderId = sender_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSent_At() {
        return sentAt;
    }

    public void setSent_At(LocalDateTime sent_At) {
        this.sentAt = sentAt;
    }


}
