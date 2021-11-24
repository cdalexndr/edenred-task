package com.edenred.data;

import java.io.Serializable;
import java.util.Objects;

class OrderProductId implements Serializable {
    private OrderEntity order;
    private ProductEntity product;

    protected OrderProductId() {}

    public OrderProductId( OrderEntity order, ProductEntity product ) {
        this.order = order;
        this.product = product;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public ProductEntity getProduct() {
        return product;
    }

    @Override
    public boolean equals( Object o ) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderProductId)) {
            return false;
        }
        OrderProductId that = (OrderProductId) o;
        return getOrder().equals( that.getOrder() ) && getProduct().equals( that.getProduct() );
    }

    @Override
    public int hashCode() {
        return Objects.hash( getOrder(), getProduct() );
    }
}
