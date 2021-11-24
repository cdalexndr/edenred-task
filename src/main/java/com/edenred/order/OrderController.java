package com.edenred.order;

import com.edenred.data.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
class OrderController {
    @Autowired OrderService orderService;

    @GetMapping("/order/{id}")
    public Order getOrder( @PathVariable long id ) {
        return orderService.getOrderById( id );
    }

    @GetMapping("/orders/user/{id}")
    public List<Order> getOrderHistory( @PathVariable long id ) {
        return orderService.getOrderHistoryForUser( id );
    }

    @GetMapping("/order/current/user/{id}")
    public Order getCurrentOrder( @PathVariable long id ) {
        return orderService.getCurrentOrderForUser( id );
    }

    @PostMapping("/order/user/{userId}")
    public void finishOrder( @PathVariable long userId ) {
        orderService.finishOrderForUser( userId );
    }

    @PutMapping("/order/user/{userId}/product/{productId}")
    public Order addProduct( @PathVariable long userId, @PathVariable long productId ) {
        return orderService.addProduct( userId, productId );
    }

    @DeleteMapping("/order/user/{userId}/product/{productId}")
    public Order removeProduct( @PathVariable long userId, @PathVariable long productId ) {
        return orderService.removeProduct( userId, productId );
    }

    @PutMapping("/order/user/{userId}/product/{productId}/quantity")
    public Order increaseQuantity( @PathVariable long userId, @PathVariable long productId ) {
        return orderService.increaseQuantity( userId, productId );
    }

    @DeleteMapping("/order/user/{userId}/product/{productId}/quantity")
    public Order decreaseQuantity( @PathVariable long userId, @PathVariable long productId ) {
        return orderService.decreaseQuantity( userId, productId );
    }
}
