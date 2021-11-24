package com.edenred.data;

import com.edenred.data.ProductService.ProductAlreadyExistsException;
import com.edenred.order.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductServiceTest {
    @Autowired ProductService productService;
    @Autowired EntityManager entityManager;

    @Test
    public void testCreateAndDto() {
        Product product = productService.create( "product", 1 );
        assertEquals( product.getName(), "product" );
        assertEquals( product.getStock(), 1 );
        assertTrue( product.getId() > 0 );
    }

    @Test
    public void testInvalidStock() {
        assertThrows( Exception.class, () -> {
            productService.create( "product", -1 );
            entityManager.flush();
        } );
    }

    @Test
    public void testDuplicate() {
        Product product = productService.create( "product", 1 );
        assertThrows( ProductAlreadyExistsException.class,
                () -> productService.create( "product", 1 ) );
    }
}
