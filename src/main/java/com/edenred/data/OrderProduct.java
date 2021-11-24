package com.edenred.data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@IdClass(OrderProductId.class)
class OrderProduct implements Serializable {

    @ManyToOne
    @Id
    @NotNull
    private OrderEntity order;

    @OneToOne
    @Id
    @NotNull
    private ProductEntity product;

    @Min(1)
    private int quantity;

    protected OrderProduct() {}

    public OrderProduct( OrderEntity order, ProductEntity product ) {
        this.order = order;
        this.product = product;
        this.quantity = 1;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increaseQuantity() {
        ++quantity;
    }

    public void decreaseQuantity() {
        --quantity;
    }

    @Override public boolean equals( Object o ) {
        if (this == o)
            return true;
        if (!(o instanceof OrderProduct))
            return false;
        OrderProduct that = (OrderProduct) o;
        return getOrder().equals( that.getOrder() )
                && getProduct().equals( that.getProduct() );
    }

    @Override public int hashCode() {
        return Objects.hash( getOrder(), getProduct() );
    }
}
