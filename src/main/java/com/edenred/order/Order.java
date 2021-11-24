package com.edenred.order;


import com.edenred.data.OrderState;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public class Order {
    private final int id;
    private final User user;
    private final OrderState state;
    private final Instant date;
    private final List<OrderItem> items;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Order( @JsonProperty("id") int id,
            @JsonProperty("user") User user,
            @JsonProperty("state") OrderState state,
            @JsonProperty("date") Instant date,
            @JsonProperty("items") List<OrderItem> items ) {
        this.id = id;
        this.user = user;
        this.state = state;
        this.date = fixPrecision( date );
        this.items = items;
    }

    //fix serialization precision
    private Instant fixPrecision( Instant date ) {
        return date == null ? null : date.truncatedTo( ChronoUnit.MILLIS );
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public OrderState getState() {
        return state;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public Instant getDate() {
        return date;
    }

    @Override public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return getUser().equals( order.getUser() ) &&
                getState() == order.getState()
                && getItems().equals( order.getItems() )
                && Objects.equals( getDate(), order.getDate() );
    }

    @Override public int hashCode() {
        return Objects.hash( getUser(), getState(), getItems(), getDate() );
    }

    @Override public String toString() {
        return String.format( "%d(%s)=%s", getId(), getUser(), getItems() );
    }
}
