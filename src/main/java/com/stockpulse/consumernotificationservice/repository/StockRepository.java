package com.stockpulse.consumernotificationservice.repository;

import com.stockpulse.consumernotificationservice.model.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends MongoRepository<Stock, String> {
    
    /**
     * Find stock by ID
     */
    Optional<Stock> findById(String stockId);
    
    /**
     * Find stock by symbol
     */
    Optional<Stock> findBySymbol(String symbol);
}