package com.edenred.data;

import com.edenred.data.EntityNotFoundException.ProductNotFoundException;
import com.edenred.order.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;

@Service
@Transactional
public class ProductService {
    @Autowired ProductRepository productRepository;
    @Autowired DtoMapper dtoMapper;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class ProductAlreadyExistsException extends RuntimeException {
        public ProductAlreadyExistsException( String name ) {
            super( String.format( "Product %s already exists", name ) );
        }
    }

    public Product create( String name, int stock ) {
        Optional<ProductEntity> existing = productRepository.findByNormName( ProductEntity.normalizeName( name ) );
        if (existing.isPresent())
            throw new ProductAlreadyExistsException( name );
        ProductEntity product = productRepository.save( new ProductEntity( name, stock ) );
        return dtoMapper.productToDto( product );
    }

    ProductEntity getProductEntity( long id ) {
        return productRepository.findById( id )
                .orElseThrow( () -> new ProductNotFoundException( id ) );
    }

    public void decreaseStock( long productId, int quantity ) {
        ProductEntity product = productRepository.findById( productId )
                .orElseThrow( () -> new ProductNotFoundException( productId ) );
        if (product.getStock() < quantity)
            throw new IllegalArgumentException( "Insufficient stock" );
        product.setStock( product.getStock() - 1 );
    }
}
