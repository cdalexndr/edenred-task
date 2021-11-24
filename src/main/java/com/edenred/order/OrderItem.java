package com.edenred.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class OrderItem {
    private final Product product;
    private final int quantity;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public OrderItem( @JsonProperty("product") Product product,
            @JsonProperty("quantity") int quantity ) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return getQuantity() == orderItem.getQuantity()
                && getProduct().equals( orderItem.getProduct() );
    }

    @Override public int hashCode() {
        return Objects.hash( getProduct(), getQuantity() );
    }

    @Override public String toString() {
        return String.format( "(%s)%dpcs", getProduct(), getQuantity() );
    }
}
