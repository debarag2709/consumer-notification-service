package com.stockpulse.consumernotificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wishlists")
public class Wishlist {
    
    @Id
    private String id; // "userId::stockId"
    
    private String userId;
    
    private String stockId;
    
    private String ruleType; // "percentage_increase", "percentage_drop"
    
    private String ruleValueInPercent; // "5%"
    
    private Double rateValueTargeted; // 2500
    
    private Double ruleValueAtSet; // 2200 - price of the stock when rule is set or updated
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private Boolean active; // will fetch the prices of stocks that are in active state
    
    private Boolean notified; // if once user is notified, will set to true
}