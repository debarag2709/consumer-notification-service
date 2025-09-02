package com.stockpulse.consumernotificationservice.service;

import com.stockpulse.consumernotificationservice.model.Stock;
import com.stockpulse.consumernotificationservice.model.User;
import com.stockpulse.consumernotificationservice.model.Wishlist;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {
    
    /**
     * Send wishlist notification email to user
     */
    public boolean sendWishlistNotification(User user, Stock stock, Wishlist wishlist) {
        try {
            String subject = "Stock Alert - " + stock.getName();
            String emailBody = buildNotificationBody(stock, wishlist);
            
            log.info("Preparing to send email notification:");
            log.info("To: {} ({})", user.getName(), user.getEmail());
            log.info("Subject: {}", subject);
            log.info("Body: {}", emailBody);
            
            // TODO: Implement actual email sending logic
            // This could integrate with services like:
            // - Spring Boot Mail Starter
            // - SendGrid
            // - AWS SES
            // - JavaMail API
            
            // For now, simulate email sending
            simulateEmailSending(user.getEmail(), subject, emailBody);
            
            log.info("Email notification sent successfully to: {}", user.getEmail());
            return true;
            
        } catch (Exception e) {
            log.error("Failed to send email notification to user: {} ({})", user.getName(), user.getEmail(), e);
            return false;
        }
    }
    
    /**
     * Build notification email body based on wishlist rule
     */
    private String buildNotificationBody(Stock stock, Wishlist wishlist) {
        String ruleDescription = buildRuleDescription(wishlist);
        
        return String.format(
            "Your wishlisted stock %s is %s. Please buy the stock quickly, before price drops or rises. Thank you for choosing Stock Pulse.",
            stock.getName(),
            ruleDescription
        );
    }
    
    /**
     * Build rule description based on wishlist rule type and value
     */
    private String buildRuleDescription(Wishlist wishlist) {
        String ruleType = wishlist.getRuleType();
        String ruleValue = wishlist.getRuleValueInPercent();
        
        switch (ruleType.toLowerCase()) {
            case "percentage_increase":
                return "up by " + ruleValue;
            case "percentage_drop":
                return "down by " + ruleValue;
            default:
                return "meeting your criteria";
        }
    }
    
    /**
     * Simulate email sending (replace with actual implementation)
     */
    private void simulateEmailSending(String toEmail, String subject, String body) {
        try {
            // Simulate network delay
            Thread.sleep(100);
            
            log.info("=== EMAIL SIMULATION ===");
            log.info("TO: {}", toEmail);
            log.info("SUBJECT: {}", subject);
            log.info("BODY: {}", body);
            log.info("========================");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Email sending interrupted", e);
        }
    }
    
    /**
     * Validate email address format
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Basic email validation
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }
}