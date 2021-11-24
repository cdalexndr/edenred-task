package com.edenred.data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Order")
class OrderEntity implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @NotNull
    private UserEntity user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "idx")
    private final List<OrderProduct> products = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderState state;

    @NotNull
    private Instant date;

    protected OrderEntity() {}

    public OrderEntity( UserEntity user ) {
        this.user = user;
        this.state = OrderState.Creating;
        this.date = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public List<OrderProduct> getProducts() {
        return products;
    }

    public boolean hasProduct( long productId ) {
        return products.stream().anyMatch( p -> p.getProduct().getId() == productId );
    }

    public void addProduct( ProductEntity product ) {
        products.add( new OrderProduct( this, product ) );
    }

    public boolean removeProduct( long productId ) {
        OrderProduct existing = products.stream()
                .filter( p -> p.getProduct().getId() == productId )
                .findAny()
                .orElse( null );
        return products.remove( existing );
    }

    public OrderState getState() {
        return state;
    }

    public void finish() {
        this.state = OrderState.Finished;
        this.date = Instant.now();
    }

    public Instant getDate() {
        return date;
    }
}
