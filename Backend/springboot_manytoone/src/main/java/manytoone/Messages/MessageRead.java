package manytoone.Messages;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import manytoone.Users.User;

/**
 * Entity to track which users have read which messages in group conversations
 */
@Entity
@Table(name = "message_reads", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"message_id", "user_id"}))
public class MessageRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "read_at", nullable = false)
    private LocalDateTime readAt = LocalDateTime.now();

    protected MessageRead() {}

    public MessageRead(Message message, User user) {
        this.message = message;
        this.user = user;
        this.readAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}
