package com.stockpulse.consumernotificationservice.repository;

import com.stockpulse.consumernotificationservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    /**
     * Find user by ID
     */
    Optional<User> findById(String userId);
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
}