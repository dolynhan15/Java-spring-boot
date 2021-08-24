package com.qooco.boost.data.oracle.services;

import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;

public interface EntryService<T, ID extends Serializable> {
    JpaRepository<T, ID> getRepository();
    default T save(T entity) {
        return getRepository().save(entity);
    }

    default List<T> save(Iterable<T> entities) {
        return getRepository().saveAll(entities);
    }

    default T findById(ID id) {
        return getRepository().findById(id).orElse(null);
    }

    default List<T> findAllById(Iterable<ID> ids) {
        return getRepository().findAllById(ids);
    }

    default List<T> findAll() {return getRepository().findAll();}
}
