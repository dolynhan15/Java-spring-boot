package com.qooco.boost.data.mongo.services;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.Serializable;
import java.util.List;

public interface DocService<T, ID extends Serializable> {
    MongoRepository<T, ID> getRepository();

    default T save(T doc) {
        return getRepository().save(doc);
    }

    default List<T> save(Iterable<T> docs) {
        return getRepository().saveAll(docs);
    }

    default T findById(ID id) {
        return getRepository().findById(id).orElse(null);
    }

    default List<T> findAllById(Iterable<ID> ids) {
        return (List<T>) getRepository().findAllById(ids);
    }

    default void deleteById(ID id) {
        getRepository().deleteById(id);
    }

    default void delete(T doc) {
        getRepository().delete(doc);
    }

    default void deleteAll(Iterable<T> docs) {
        getRepository().deleteAll(docs);
    }
}
