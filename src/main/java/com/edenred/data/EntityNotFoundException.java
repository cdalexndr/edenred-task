package com.edenred.data;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public abstract class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException( String name, long id ) {
        super( String.format( "Missing %s with id %d", name, id ) );
    }

    public static class UserNotFoundException extends EntityNotFoundException {
        public UserNotFoundException( long id ) {
            super( "User", id );
        }
    }

    public static class ProductNotFoundException extends EntityNotFoundException {
        public ProductNotFoundException( long id ) {
            super( "Product", id );
        }
    }

    public static class OrderNotFoundException extends EntityNotFoundException {
        public OrderNotFoundException( long id ) {
            super( "Order", id );
        }
    }
}
