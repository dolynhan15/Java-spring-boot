package com.qooco.boost.data.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface Boot2MongoRepository<T, ID extends Serializable> extends MongoRepository<T, ID> {
    /*default Optional<T> findById(ID id) {
        return ofNullable(findOne(id));
    }

    default <S extends T> List<S> saveAll(Iterable<S> entites) {
        return save(entites);
    }

    default void deleteById(ID id) {
        delete(id);
    }

    default void deleteAll(Iterable<? extends T> entities) {
        delete(entities);
    }*/
}
