package com.stockpulse.consumernotificationservice.service;

import com.stockpulse.consumernotificationservice.exception.WishlistProcessingException;
import com.stockpulse.consumernotificationservice.model.QStacksMessage;
import com.stockpulse.consumernotificationservice.model.Stock;
import com.stockpulse.consumernotificationservice.model.User;
import com.stockpulse.consumernotificationservice.model.Wishlist;
import com.stockpulse.consumernotificationservice.repository.StockRepository;
import com.stockpulse.consumernotificationservice.repository.UserRepository;
import com.stockpulse.consumernotificationservice.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistNotificationProcessor {
    
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final WishlistRepository wishlistRepository;
    private final EmailService emailService;
    
    /**
     * Process QStacks message and send wishlist notification
     */
    public void processWishlistNotification(QStacksMessage qStacksMessage) {
        log.info("Processing QStacks message with wishlist ID: {}", qStacksMessage.getId());
        
        try {
            // Step 3: Extract user_id and stock_id from wishlist ID
            String userId = qStacksMessage.getUserId();
            String stockId = qStacksMessage.getStockId();
            
            if (userId == null || stockId == null) {
                throw new WishlistProcessingException("Invalid wishlist ID format: " + qStacksMessage.getId());
            }
            
            log.info("Extracted - User ID: {}, Stock ID: {}", userId, stockId);
            
            // Step 4: Fetch user email from user table using user_id
            User user = fetchUserById(userId);
            validateUserEmail(user);
            
            // Step 5: Fetch stock details from stock table using stock_id
            Stock stock = fetchStockById(stockId);
            
            // Step 6: Fetch the rule from wishlist table using the wishlist id
            Wishlist wishlist = fetchWishlistById(qStacksMessage.getId());
            
            // Step 7: Trigger notifications user based on the stocks and their rule
            boolean emailSent = sendNotification(user, stock, wishlist);
            
            if (!emailSent) {
                throw new WishlistProcessingException("Failed to send email notification to user: " + user.getEmail());
            }
            
            // Step 8: Once notification is sent successfully, update the notified field to true
            updateWishlistNotifiedStatus(wishlist);
            
            log.info("Successfully processed wishlist notification for user: {} and stock: {}", 
                    user.getEmail(), stock.getName());
            
        } catch (WishlistProcessingException e) {
            log.error("Error processing wishlist notification for ID: {}", qStacksMessage.getId(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error processing wishlist notification for ID: {}", qStacksMessage.getId(), e);
            throw new WishlistProcessingException("Unexpected error processing wishlist notification", e);
        }
    }
    
    /**
     * Step 4: Fetch user by ID and validate
     */
    private User fetchUserById(String userId) {
        log.info("Fetching user with ID: {}", userId);
        
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    String errorMsg = "User not found with ID: " + userId;
                    log.error(errorMsg);
                    return new WishlistProcessingException(errorMsg);
                });
    }
    
    /**
     * Step 4: Validate user email
     */
    private void validateUserEmail(User user) {
        if (!emailService.isValidEmail(user.getEmail())) {
            String errorMsg = "Invalid or missing email for user: " + user.getId() + ", email: " + user.getEmail();
            log.error(errorMsg);
            throw new WishlistProcessingException(errorMsg);
        }
        log.info("User email validated: {}", user.getEmail());
    }
    
    /**
     * Step 5: Fetch stock by ID and validate
     */
    private Stock fetchStockById(String stockId) {
        log.info("Fetching stock with ID: {}", stockId);
        
        return stockRepository.findById(stockId)
                .orElseThrow(() -> {
                    String errorMsg = "Stock not found with ID: " + stockId;
                    log.error(errorMsg);
                    return new WishlistProcessingException(errorMsg);
                });
    }
    
    /**
     * Step 6: Fetch wishlist by ID and validate
     */
    private Wishlist fetchWishlistById(String wishlistId) {
        log.info("Fetching wishlist with ID: {}", wishlistId);
        
        return wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> {
                    String errorMsg = "Wishlist not found with ID: " + wishlistId;
                    log.error(errorMsg);
                    return new WishlistProcessingException(errorMsg);
                });
    }
    
    /**
     * Step 7: Send notification to user
     */
    private boolean sendNotification(User user, Stock stock, Wishlist wishlist) {
        log.info("Sending notification to user: {} for stock: {}", user.getEmail(), stock.getName());
        
        try {
            return emailService.sendWishlistNotification(user, stock, wishlist);
        } catch (Exception e) {
            String errorMsg = "Failed to send notification to user: " + user.getEmail();
            log.error(errorMsg, e);
            throw new WishlistProcessingException(errorMsg, e);
        }
    }
    
    /**
     * Step 8: Update wishlist notified status to true
     */
    private void updateWishlistNotifiedStatus(Wishlist wishlist) {
        log.info("Updating wishlist notified status to true for ID: {}", wishlist.getId());
        
        try {
            wishlist.setNotified(true);
            wishlist.setUpdatedAt(LocalDateTime.now());
            wishlistRepository.save(wishlist);
            
            log.info("Successfully updated wishlist notified status for ID: {}", wishlist.getId());
        } catch (Exception e) {
            String errorMsg = "Failed to update wishlist notified status for ID: " + wishlist.getId();
            log.error(errorMsg, e);
            throw new WishlistProcessingException(errorMsg, e);
        }
    }
}