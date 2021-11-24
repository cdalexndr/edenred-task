package com.edenred.data;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Set;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InsufficientStockException extends RuntimeException {
    private final Set<Long> productIds;

    public InsufficientStockException( Set<Long> productIds ) {
        this.productIds = productIds;
    }

    public Set<Long> getProductIds() {
        return productIds;
    }
}
