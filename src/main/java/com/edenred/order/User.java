package com.edenred.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class User {
    private final long id;
    private final String name;
    private final String email;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public User( @JsonProperty("id") long id,
            @JsonProperty("name") String name,
            @JsonProperty("email")String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getId() == user.getId()
                && getName().equals( user.getName() )
                && getEmail().equals( user.getEmail() );
    }

    @Override public int hashCode() {
        return Objects.hash( getId(), getName(), getEmail() );
    }

    @Override public String toString() {
        return name;
    }
}
