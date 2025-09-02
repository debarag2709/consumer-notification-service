package com.stockpulse.consumernotificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "stocks")
public class Stock {
    
    @Id
    private String id; // uniquecode
    
    private String symbol;
    
    private String name;
    
    private Double currentPrice;
    
    private String exchange;
    
    private String sector;
}