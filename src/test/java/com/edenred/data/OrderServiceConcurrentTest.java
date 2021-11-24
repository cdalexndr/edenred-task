package com.edenred.data;

import com.edenred.BaseDataTest;
import com.edenred.order.Order;
import com.edenred.order.Product;
import com.edenred.order.User;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class OrderServiceConcurrentTest extends BaseDataTest {
    @Autowired OrderService orderService;
    @Autowired UserService userService;
    @Autowired ProductService productService;

    @RepeatedTest(3)
    public void testUpdateStockConcurrentOrders() throws Exception {
        int count = 10;
        List<User> users = IntStream.range( 0, count )
                .mapToObj( i -> userService.create( "user" + i, "user" + i + "@mail.com" ) )
                .collect( toList() );
        Product product = productService.create( "product", count );

        AtomicInteger sync = new AtomicInteger( count );
        List<Throwable> errors = Collections.synchronizedList( new ArrayList<>() );
        List<Thread> threads = users.stream()
                .map( user -> new Thread( () -> {
                    try {
                        Order order = orderService.getCurrentOrderForUser( user.getId() );
                        order = orderService.addProduct( user.getId(), product.getId() );
                        sync.decrementAndGet();
                        while (sync.get() != 0)
                            Thread.sleep( 1 );
                        orderService.finishOrderForUser( user.getId() );
                    } catch (Exception e) {
                        throw new RuntimeException( e );
                    }
                } ) ).collect( toList() );
        threads.forEach( t -> t.setUncaughtExceptionHandler( ( t1, e ) -> errors.add( e ) ) );

        try {
            threads.forEach( Thread::start );
            for (Thread thread : threads)
                thread.join();
            assertEquals( 0, errors.size(), errors.stream().map( Throwable::toString ).collect( joining( "\n" ) ) );
            assertEquals( 0, sync.get() );
            assertEquals( 0, productService.getProductEntity( product.getId() ).getStock() );
        } finally {
            threads.forEach( Thread::interrupt );
        }
    }
}
