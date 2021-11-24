package com.edenred.data;

import com.edenred.data.EntityNotFoundException.OrderNotFoundException;
import com.edenred.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    @Autowired OrderRepository orderRepository;
    @Autowired ProductRepository productRepository;
    @Autowired UserRepository userRepository;
    @Autowired DtoMapper dtoMapper;
    @Autowired UserService userService;
    @Autowired ProductService productService;
    @Autowired EntityManager em;

    /** returns previous orders sorted by oldest first **/
    public List<Order> getOrderHistoryForUser( long userId ) {
        return orderRepository.findByUser_IdOrderByDate( userId )
                .stream().filter( o -> o.getState() != OrderState.Creating )
                .map( dtoMapper::orderToDto )
                .collect( Collectors.toList() );
    }


    private @NotNull OrderEntity getOrderForUser( long userId ) {
        return orderRepository.findByUser_IdAndState( userId, OrderState.Creating )
                .orElseGet( () -> createNewOrder( userId ) );
    }

    private @NotNull OrderEntity createNewOrder( long userId ) {
        UserEntity userEntity = userService.getUserEntity( userId );
        return orderRepository.save( new OrderEntity( userEntity ) );
    }

    public @NotNull Order getOrderById( long orderId ) {
        OrderEntity order = orderRepository.findById( orderId )
                .orElseThrow( () -> new OrderNotFoundException( orderId ) );
        return dtoMapper.orderToDto( order );
    }

    public Order getCurrentOrderForUser( long userId ) {
        return dtoMapper.orderToDto( getOrderForUser( userId ) );
    }

    public Order addProduct( long userId, long productId ) {
        OrderEntity order = getOrderForUser( userId );
        if (order.hasProduct( productId ))
            return increaseQuantity( userId, productId );
        else {
            ProductEntity product = productService.getProductEntity( productId );
            order.addProduct( product );
            return dtoMapper.orderToDto( order );
        }
    }

    public Order removeProduct( long userId, long productId ) {
        OrderEntity order = getOrderForUser( userId );
        order.removeProduct( productId );
        return dtoMapper.orderToDto( order );
    }

    public Order increaseQuantity( long userId, long productId ) {
        OrderEntity order = getOrderForUser( userId );
        ProductEntity product = productService.getProductEntity( productId );
        Optional<OrderProduct> orderProduct = order.getProducts().stream()
                .filter( p -> p.getProduct().getId() == productId )
                .findAny();
        if (orderProduct.isPresent()) {
            int expectedQuantity = orderProduct.get().getQuantity() + 1;
            if (product.getStock() < expectedQuantity)
                throw new IllegalArgumentException( "Insufficient stock" );
            orderProduct.get().increaseQuantity();
        }
        return dtoMapper.orderToDto( order );
    }

    public Order decreaseQuantity( long userId, long productId ) {
        OrderEntity order = getOrderForUser( userId );
        Optional<OrderProduct> orderProduct = order.getProducts().stream()
                .filter( p -> p.getProduct().getId() == productId )
                .findAny();
        if (orderProduct.isPresent()) {
            orderProduct.get().decreaseQuantity();
            if (orderProduct.get().getQuantity() <= 0)
                order.removeProduct( productId );
        }
        return dtoMapper.orderToDto( order );
    }

    public Order finishOrderForUser( long userId ) {
        OrderEntity order = getOrderForUser( userId );
        validateOrder( order );
        decreaseStocks( order );
        order.finish();
        return dtoMapper.orderToDto( order );
    }

    private void decreaseStocks( OrderEntity order ) {
        order.getProducts().forEach( p ->
                productService.decreaseStock( p.getProduct().getId(), p.getQuantity() ) );
    }

    private void validateOrder( OrderEntity order ) {
        if (order.getProducts().isEmpty())
            throw new IllegalArgumentException( "No product ordered" );
        validateProductStocks( order );
    }

    private void validateProductStocks( OrderEntity order ) {
        List<OrderProduct> insufficientStockProducts = order.getProducts().stream()
                .filter( p -> p.getProduct().getStock() < p.getQuantity() )
                .collect( Collectors.toList() );
        if (!insufficientStockProducts.isEmpty())
            throw new InsufficientStockException( insufficientStockProducts.stream()
                    .map( p -> p.getProduct().getId() )
                    .collect( Collectors.toSet() ) );
    }
}
