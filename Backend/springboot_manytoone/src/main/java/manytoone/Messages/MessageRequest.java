package manytoone.Messages;

public class MessageRequest {

        private int conversationId;
        private int senderId;
        private String content;

        public int getConversationId() {
            return conversationId;
        }

        public void setConversationId(int conversationId) {
            this.conversationId = conversationId;
        }

        public int getSenderId() {
            return senderId;
        }

        public void setSenderId(int senderId) {
            this.senderId = senderId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
}
