package com.edenred.data;

import com.edenred.order.Order;
import com.edenred.order.OrderItem;
import com.edenred.order.Product;
import com.edenred.order.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestInstance(Lifecycle.PER_CLASS)
class OrderServiceTest {
    @Autowired UserService userService;
    @Autowired OrderService orderService;
    @Autowired ProductService productService;
    @Autowired UserRepository userRepository;
    private User user;

    @BeforeAll
    public void init() {
        user = userService.create( "user", "a@b.com" );
    }

    @AfterAll
    public void cleanup() {
        userRepository.deleteById( user.getId() );
    }

    @Test
    public void testCreateCurrentOrder() {
        Order order = orderService.getCurrentOrderForUser( user.getId() );
        assertEquals( order.getState(), OrderState.Creating );
        assertEquals( order.getItems().size(), 0 );
        assertEquals( order.getUser().getId(), user.getId() );
    }

    @Test
    public void testGetById() {
        Order expected = orderService.getCurrentOrderForUser( user.getId() );
        Order order = orderService.getOrderById( expected.getId() );
        assertEquals( expected, order );
    }

    @Test
    public void testOrderHistory() {
        Product product = productService.create( "product", 2 );

        Order firstOrder = orderService.getCurrentOrderForUser( user.getId() );
        firstOrder = orderService.addProduct( user.getId(), product.getId() );
        firstOrder = orderService.finishOrderForUser( user.getId() );

        Order secondOrder = orderService.getCurrentOrderForUser( user.getId() );
        secondOrder = orderService.addProduct( user.getId(), product.getId() );
        secondOrder = orderService.finishOrderForUser( user.getId() );
        assertNotEquals( firstOrder.getId(), secondOrder.getId() );

        List<Order> orders = orderService.getOrderHistoryForUser( user.getId() );
        assertEquals( orders, List.of( firstOrder, secondOrder ) );
    }

    @Test
    public void testAddProduct() {
        Product product = productService.create( "product", 2 );
        Order order = orderService.addProduct( user.getId(), product.getId() );
        assertEquals( order.getItems().size(), 1 );

        OrderItem item = order.getItems().iterator().next();
        assertEquals( item.getProduct().getId(), product.getId() );
        assertEquals( item.getQuantity(), 1 );

        order = orderService.addProduct( user.getId(), product.getId() );
        assertEquals( order.getItems().size(), 1 );
        assertEquals( order.getItems().get( 0 ).getQuantity(), 2 );
    }

    @Test
    public void testIncreaseDecreaseQuantity() {
        Product product = productService.create( "product", 2 );
        Order order = orderService.addProduct( user.getId(), product.getId() );
        assertEquals( order.getItems().size(), 1 );
        assertEquals( order.getItems().get( 0 ).getQuantity(), 1 );

        order = orderService.increaseQuantity( user.getId(), product.getId() );
        assertEquals( order.getItems().size(), 1 );
        assertEquals( order.getItems().get( 0 ).getQuantity(), 2 );

        order = orderService.decreaseQuantity( user.getId(), product.getId() );
        assertEquals( order.getItems().size(), 1 );
        assertEquals( order.getItems().get( 0 ).getQuantity(), 1 );
    }

    @Test
    public void testDecreaseQuantityRemovesProduct() {
        Product product = productService.create( "product", 1 );
        Order order = orderService.addProduct( user.getId(), product.getId() );
        assertEquals( order.getItems().size(), 1 );
        assertEquals( order.getItems().get( 0 ).getQuantity(), 1 );

        order = orderService.decreaseQuantity( user.getId(), product.getId() );
        assertEquals( order.getItems().size(), 0 );
    }

    @Test
    public void testRemoveProduct() {
        Product product = productService.create( "product", 1 );
        Order order = orderService.addProduct( user.getId(), product.getId() );
        assertEquals( order.getItems().size(), 1 );
        assertEquals( order.getItems().get( 0 ).getQuantity(), 1 );

        order = orderService.removeProduct( user.getId(), product.getId() );
        assertEquals( order.getItems().size(), 0 );
    }

    @Test
    public void testFinishOrder() {
        Product product = productService.create( "product", 1 );
        Order order = orderService.addProduct( user.getId(), product.getId() );
        order = orderService.finishOrderForUser( user.getId() );
        assertNotNull( order.getDate() );
        assertEquals( order.getState(), OrderState.Finished );

        Order newOrder = orderService.getCurrentOrderForUser( user.getId() );
        assertNotEquals( order.getId(), newOrder.getId() );
    }

    @Test
    public void testTwoOrdersSameProduct() {
        User user1 = userService.create( "user1", "u1@mail.com" );
        User user2 = userService.create( "user2", "u2@mail.com" );
        Product product = productService.create( "product", 2 );

        Order order1 = orderService.getCurrentOrderForUser( user1.getId() );
        orderService.addProduct( user1.getId(), product.getId() );

        Order order2 = orderService.getCurrentOrderForUser( user2.getId() );
        orderService.addProduct( user2.getId(), product.getId() );

        orderService.finishOrderForUser( user1.getId() );
        orderService.finishOrderForUser( user2.getId() );

        assertEquals( productService.getProductEntity( product.getId() ).getStock(), 0 );
    }

    @Test
    public void testCannotFinishEmptyOrder() {
        Order order = orderService.getCurrentOrderForUser( user.getId() );
        assertThrows( Exception.class, () -> orderService.finishOrderForUser( user.getId() ) );
    }

    @Test
    public void testInsufficientStockOnOrderFinish() {
        User user1 = userService.create( "user1", "u1@mail.com" );
        User user2 = userService.create( "user2", "u2@mail.com" );
        Product product = productService.create( "product", 1 );

        Order order1 = orderService.getCurrentOrderForUser( user1.getId() );
        orderService.addProduct( user1.getId(), product.getId() );

        Order order2 = orderService.getCurrentOrderForUser( user2.getId() );
        orderService.addProduct( user2.getId(), product.getId() );

        orderService.finishOrderForUser( user1.getId() );
        assertThrows( InsufficientStockException.class,
                () -> orderService.finishOrderForUser( user2.getId() )
        );

        assertEquals( productService.getProductEntity( product.getId() ).getStock(), 0 );
    }
}