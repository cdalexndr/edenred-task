package com.edenred.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Product {
    private final int id;
    private final String name;
    private final int stock;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Product( @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("stock") int stock ) {
        this.id = id;
        this.name = name;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }

    @Override public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return getId() == product.getId()
                && getName().equals( product.getName() );
    }

    @Override public int hashCode() {
        return Objects.hash( getId(), getName() );
    }

    @Override public String toString() {
        return String.format( "%d=%s", getId(), getName() );
    }
}
