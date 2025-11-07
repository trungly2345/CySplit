package manytoone.Conversations;

import jakarta.persistence.*;
import manytoone.Users.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
public class Conversation {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "conversation_id", nullable = false)
        private int conversationId;

        // "GROUP" or "DIRECT"
        @Column(name = "conversation_type", nullable = false)
        private String conversationType = "GROUP";

        // for group chats, can be null for DMs
        @Column(name = "name")
        private String name;

        @ManyToOne
        @JoinColumn(name = "created_by")
        private User createdBy;

        @Column(name = "created_at", nullable = false)
        private LocalDateTime createdAt = LocalDateTime.now();

        protected Conversation() {}

        public Conversation(String conversationType, String name, User createdBy) {
            this.conversationType = conversationType;
            this.name = name;
            this.createdBy = createdBy;
            this.createdAt = LocalDateTime.now();
        }

        public int getConversationId() {
            return conversationId;
        }

        public String getConversationType() {
            return conversationType;
        }

        public void setConversationType(String conversationType) {
            this.conversationType = conversationType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public User getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(User createdBy) {
            this.createdBy = createdBy;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

