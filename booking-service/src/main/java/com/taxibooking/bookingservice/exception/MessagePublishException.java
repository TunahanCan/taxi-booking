package com.taxibooking.bookingservice.exception;

public class MessagePublishException extends RuntimeException {
    private final String topic;
    private final String messageKey;
    
    public MessagePublishException(String message, String topic, String messageKey, Throwable cause) {
        super(message, cause);
        this.topic = topic;
        this.messageKey = messageKey;
    }
    
    public String getTopic() {
        return topic;
    }
    
    public String getMessageKey() {
        return messageKey;
    }
} 