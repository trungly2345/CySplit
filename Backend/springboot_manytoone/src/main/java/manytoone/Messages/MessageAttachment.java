package manytoone.Messages;

import jakarta.persistence.*;

@Entity
@Table(name = "message_attachments")
public class MessageAttachment {



        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int attachmentId;

        @Column(nullable = false)
        private int messageId;

        @Column(nullable = false)
        private String fileUrl;

        private String fileType;

    public int getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(int attachmentId) {
        this.attachmentId = attachmentId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }



}
