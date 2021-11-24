package com.edenred.data;

import com.edenred.order.Order;
import com.edenred.order.OrderItem;
import com.edenred.order.Product;
import com.edenred.order.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DtoMapper {

    User userToDto( UserEntity user );

    Product productToDto( ProductEntity entity );

    @Mapping(source = "products", target = "items")
    Order orderToDto( OrderEntity order );

    OrderItem orderItemToDto( OrderProduct orderItem );
}
