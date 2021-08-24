package com.qooco.boost.data.oracle.reposistories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface Boot2JpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
    /*default <S extends T> List<S> saveAll(Iterable<S> iterable) {
        return save(iterable);
    }

    default Optional<T> findById(ID id) {
        return ofNullable(findOne(id));
    }

    default List<T> findAllById(Iterable<ID> iterable) {
        return findAll(iterable);
    }

    default boolean existsById(ID id) {
        return exists(id);
    }

    default void deleteById(ID id) {
        delete(id);
    }*/
}
