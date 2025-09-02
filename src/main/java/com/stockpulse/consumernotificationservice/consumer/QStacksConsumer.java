package com.stockpulse.consumernotificationservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockpulse.consumernotificationservice.exception.WishlistProcessingException;
import com.stockpulse.consumernotificationservice.model.QStacksMessage;
import com.stockpulse.consumernotificationservice.service.WishlistNotificationProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * QStacks Queue Consumer for processing wishlist notification messages
 * Queue name: QStacks
 * Message format: { "id": "ABC::PQR" } where ABC is userId and PQR is stockId
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QStacksConsumer {
    
    private final WishlistNotificationProcessor wishlistNotificationProcessor;
    private final ObjectMapper objectMapper;
    
    /**
     * Generic message consumer method for QStacks queue
     * Adapt this for your specific queue system (RabbitMQ, Kafka, SQS, etc.)
     */
    public void consumeQStacksMessage(String messagePayload) {
        log.info("Received QStacks message: {}", messagePayload);
        
        try {
            // Parse the message payload
            QStacksMessage qStacksMessage = parseMessage(messagePayload);
            
            // Validate the message
            validateMessage(qStacksMessage);
            
            // Process the wishlist notification
            wishlistNotificationProcessor.processWishlistNotification(qStacksMessage);
            
            log.info("Successfully processed QStacks message: {}", qStacksMessage.getId());
            
        } catch (WishlistProcessingException e) {
            log.error("Business logic error processing QStacks message: {}", messagePayload, e);
            // Handle business logic errors - might want to send to dead letter queue
            handleBusinessError(messagePayload, e);
            
        } catch (Exception e) {
            log.error("Unexpected error processing QStacks message: {}", messagePayload, e);
            // Handle unexpected errors - might want to retry or alert
            handleUnexpectedError(messagePayload, e);
        }
    }
    
    /**
     * For RabbitMQ integration - uncomment and configure
     */
    /*
    @RabbitListener(queues = "QStacks")
    public void consumeFromRabbitMQ(String message) {
        consumeQStacksMessage(message);
    }
    */
    
    /**
     * For Kafka integration - uncomment and configure
     */
    /*
    @KafkaListener(topics = "QStacks")
    public void consumeFromKafka(String message) {
        consumeQStacksMessage(message);
    }
    */
    
    /**
     * For AWS SQS integration - you would use AWS SDK
     */
    /*
    @SqsListener("QStacks")
    public void consumeFromSQS(String message) {
        consumeQStacksMessage(message);
    }
    */
    
    /**
     * Parse JSON message to QStacksMessage object
     */
    private QStacksMessage parseMessage(String messagePayload) {
        try {
            return objectMapper.readValue(messagePayload, QStacksMessage.class);
        } catch (Exception e) {
            log.error("Failed to parse QStacks message: {}", messagePayload, e);
            throw new WishlistProcessingException("Invalid message format: " + messagePayload, e);
        }
    }
    
    /**
     * Validate QStacks message
     */
    private void validateMessage(QStacksMessage message) {
        if (message == null) {
            throw new WishlistProcessingException("QStacks message is null");
        }
        
        if (message.getId() == null || message.getId().trim().isEmpty()) {
            throw new WishlistProcessingException("QStacks message ID is null or empty");
        }
        
        if (!message.getId().contains("::")) {
            throw new WishlistProcessingException("Invalid QStacks message ID format. Expected format: 'userId::stockId', got: " + message.getId());
        }
        
        String userId = message.getUserId();
        String stockId = message.getStockId();
        
        if (userId == null || userId.trim().isEmpty()) {
            throw new WishlistProcessingException("User ID is null or empty in message: " + message.getId());
        }
        
        if (stockId == null || stockId.trim().isEmpty()) {
            throw new WishlistProcessingException("Stock ID is null or empty in message: " + message.getId());
        }
        
        log.info("QStacks message validated successfully - Wishlist ID: {}, User ID: {}, Stock ID: {}", 
                message.getId(), userId, stockId);
    }
    
    /**
     * Handle business logic errors
     */
    private void handleBusinessError(String messagePayload, WishlistProcessingException e) {
        log.warn("Business error occurred, message might need manual intervention: {}", messagePayload);
        
        // TODO: Implement error handling strategy:
        // 1. Send to dead letter queue for manual review
        // 2. Store in error log table
        // 3. Send alert to monitoring system
        // 4. Update metrics
        
        // For now, just log the error
        log.error("Business error details: {}", e.getMessage());
    }
    
    /**
     * Handle unexpected system errors
     */
    private void handleUnexpectedError(String messagePayload, Exception e) {
        log.error("Unexpected error occurred, might need retry: {}", messagePayload);
        
        // TODO: Implement retry strategy:
        // 1. Retry with exponential backoff
        // 2. Send to dead letter queue after max retries
        // 3. Alert monitoring system
        // 4. Update metrics
        
        // For now, just log the error
        log.error("Unexpected error details: {}", e.getMessage());
    }
    
    /**
     * Manual message processing for testing or direct invocation
     */
    public void processMessageDirectly(QStacksMessage qStacksMessage) {
        try {
            validateMessage(qStacksMessage);
            wishlistNotificationProcessor.processWishlistNotification(qStacksMessage);
            log.info("Direct processing completed successfully for: {}", qStacksMessage.getId());
        } catch (Exception e) {
            log.error("Direct processing failed for: {}", qStacksMessage.getId(), e);
            throw e;
        }
    }
}