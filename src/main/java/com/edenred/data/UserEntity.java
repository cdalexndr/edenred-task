package com.edenred.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "User")
class UserEntity implements Serializable {

    @Id
    @GeneratedValue
    private long id;
    @NotBlank
    private String name;
    @Email
    private String email;

    protected UserEntity() {}

    public UserEntity( String name, String email ) {
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    @Override public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        //email business key
        return getEmail().equals( that.getEmail() );
    }

    @Override public int hashCode() {
        return Objects.hash( getEmail() );
    }
}
