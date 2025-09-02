package com.stockpulse.consumernotificationservice.controller;

import com.stockpulse.consumernotificationservice.consumer.QStacksConsumer;
import com.stockpulse.consumernotificationservice.model.QStacksMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for testing wishlist notification processing
 */
@Slf4j
@RestController
@RequestMapping("/api/wishlist-notifications")
@RequiredArgsConstructor
public class WishlistNotificationController {
    
    private final QStacksConsumer qStacksConsumer;
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Wishlist Notification Service is running");
    }
    
    /**
     * Process QStacks message directly (for testing)
     */
    @PostMapping("/process")
    public ResponseEntity<String> processWishlistNotification(@RequestBody QStacksMessage qStacksMessage) {
        try {
            log.info("Processing wishlist notification directly via REST API for wishlist ID: {}", 
                    qStacksMessage.getId());
            
            qStacksConsumer.processMessageDirectly(qStacksMessage);
            
            return ResponseEntity.ok("Wishlist notification processed successfully");
        } catch (Exception e) {
            log.error("Error processing wishlist notification via REST API", e);
            return ResponseEntity.internalServerError()
                    .body("Error processing wishlist notification: " + e.getMessage());
        }
    }
    
    /**
     * Process QStacks message from JSON string (for testing queue message format)
     */
    @PostMapping("/process-json")
    public ResponseEntity<String> processWishlistNotificationFromJson(@RequestBody String jsonMessage) {
        try {
            log.info("Processing wishlist notification from JSON: {}", jsonMessage);
            
            qStacksConsumer.consumeQStacksMessage(jsonMessage);
            
            return ResponseEntity.ok("Wishlist notification processed successfully from JSON");
        } catch (Exception e) {
            log.error("Error processing wishlist notification from JSON", e);
            return ResponseEntity.internalServerError()
                    .body("Error processing wishlist notification: " + e.getMessage());
        }
    }
    
    /**
     * Test endpoint with sample wishlist ID
     */
    @GetMapping("/test/{wishlistId}")
    public ResponseEntity<String> testWishlistNotification(@PathVariable String wishlistId) {
        try {
            QStacksMessage testMessage = QStacksMessage.builder()
                    .id(wishlistId)
                    .build();
            
            log.info("Testing wishlist notification for ID: {}", wishlistId);
            
            qStacksConsumer.processMessageDirectly(testMessage);
            
            return ResponseEntity.ok("Test wishlist notification processed successfully for ID: " + wishlistId);
        } catch (Exception e) {
            log.error("Error in test wishlist notification", e);
            return ResponseEntity.internalServerError()
                    .body("Error in test: " + e.getMessage());
        }
    }
}