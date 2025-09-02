package com.stockpulse.consumernotificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QStacksMessage {
    
    private String id; // "ABC::PQR" (wishlist id)
    
    /**
     * Extract user ID from the wishlist ID
     * @return user ID (first part before ::)
     */
    public String getUserId() {
        if (id != null && id.contains("::")) {
            return id.split("::")[0];
        }
        return null;
    }
    
    /**
     * Extract stock ID from the wishlist ID
     * @return stock ID (second part after ::)
     */
    public String getStockId() {
        if (id != null && id.contains("::")) {
            String[] parts = id.split("::");
            if (parts.length > 1) {
                return parts[1];
            }
        }
        return null;
    }
}