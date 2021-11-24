package com.edenred.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface OrderRepository extends CrudRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByUser_IdAndState( long userId, OrderState state );
    List<OrderEntity> findByUser_IdOrderByDate( long userId );
}
