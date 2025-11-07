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
    @Column(name = "message_id" , nullable = false)
    private int message_id; // Primary key

    @Column(name = "conversation_id" , nullable = false)
    private int conversation_id;

    @ManyToOne
    @JoinColumn(name = "sender_id" , nullable = false)
    private User sender;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "sent_At", nullable = false)
    private LocalDateTime sent_At;

    protected Message(){}

    public Message(int conversation_id, User sender, String content){
        this.conversation_id = conversation_id;
        this.sender = sender;
        this.content = content;
    }


    public int getConversationId() {
        return conversation_id;
    }

    public void setConversation(int conversation_id) {
        this.conversation_id = conversation_id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSent_At() {
        return sent_At;
    }

    public void setSent_At(LocalDateTime sent_At) {
        this.sent_At = sent_At;
    }


}
