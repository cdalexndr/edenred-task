package com.edenred.order;

import com.edenred.data.OrderService;
import com.edenred.data.ProductService;
import com.edenred.data.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TestOrderBuilderService {
    private final AtomicInteger idx = new AtomicInteger( 1 );
    @Autowired UserService userService;
    @Autowired ProductService productService;
    @Autowired OrderService orderService;

    public TestOrderBuilder builder( User user ) {
        return new TestOrderBuilder( productService, orderService, user );
    }

    public TestOrderBuilder builder() {
        String username = "user" + idx.incrementAndGet();
        String email = username + "@mail.com";
        User user = userService.get( email ).orElseGet( () -> userService.create( username, email ) );
        return builder( user );
    }

    public static class TestOrderBuilder {
        private final ProductService productService;
        private final OrderService orderService;
        private final User user;
        private Order order;

        public TestOrderBuilder( ProductService productService,
                OrderService orderService,
                User user ) {
            this.productService = productService;
            this.orderService = orderService;
            this.user = user;
        }

        public TestOrderBuilder product( String name, int quantity ) {
            return product( name, quantity, quantity );
        }

        public TestOrderBuilder product( String name, int quantity, int available ) {
            Product product = productService.create( name, available );
            order = orderService.addProduct( user.getId(), product.getId() );
            for (int i = 1; i < quantity; ++i)
                order = orderService.increaseQuantity( user.getId(), product.getId() );
            return this;
        }

        public TestOrderBuilder finishedOrder() {
            order = orderService.finishOrderForUser( user.getId() );
            return this;
        }

        public Order build() {
            return order != null ? order : orderService.getCurrentOrderForUser( user.getId() );
        }
    }
}
