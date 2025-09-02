package com.stockpulse.consumernotificationservice.repository;

import com.stockpulse.consumernotificationservice.model.Wishlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends MongoRepository<Wishlist, String> {
    
    /**
     * Find wishlist by ID (wishlist ID = "userId::stockId")
     */
    Optional<Wishlist> findById(String wishlistId);
    
    /**
     * Find all wishlists for a user
     */
    List<Wishlist> findByUserId(String userId);
    
    /**
     * Find all active wishlists for a user
     */
    List<Wishlist> findByUserIdAndActiveTrue(String userId);
    
    /**
     * Find all active wishlists that haven't been notified
     */
    @Query("{ 'active' : true, 'notified' : false }")
    List<Wishlist> findActiveUnnotifiedWishlists();
    
    /**
     * Find wishlist by user ID and stock ID
     */
    List<Wishlist> findByUserIdAndStockId(String userId, String stockId);
}