package com.stockpulse.consumernotificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    
    @Id
    private String id; // uniquecode
    
    private String name;
    
    private String email;
    
    private String phone;
    
    @Field("password_hash")
    private String passwordHash;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}