package com.edenred.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "Product")
class ProductEntity implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @NotBlank
    private String name;

    @NotBlank
    @NotNull
    private String normName;

    @Min(0)
    private int stock;

    @Version
    private int version;

    protected ProductEntity() {}

    public ProductEntity( String name, int stock ) {
        this.name = name;
        this.normName = normalizeName( name );
        this.stock = stock;
    }

    static String normalizeName( String name ) {
        return name.toLowerCase()
                .replaceAll( "\\s++", "" );
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }

    public boolean isOutOfStock() {
        return stock == 0;
    }

    public void setStock( int stock ) {
        this.stock = stock;
    }

    String getNormName() {
        return normName;
    }

    @Override public boolean equals( Object o ) {
        if (this == o)
            return true;
        if (!(o instanceof ProductEntity))
            return false;
        ProductEntity that = (ProductEntity) o;
        //business key is normalized name property
        return getNormName().equals( that.getNormName() );
    }

    @Override public int hashCode() {
        return Objects.hash( getNormName() );
    }
}
