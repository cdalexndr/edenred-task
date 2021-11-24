package com.edenred.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface ProductRepository extends CrudRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByNormName( String name );
}
