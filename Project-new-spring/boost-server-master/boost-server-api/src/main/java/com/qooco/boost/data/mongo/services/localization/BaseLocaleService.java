package com.qooco.boost.data.mongo.services.localization;

import java.util.List;

public interface BaseLocaleService<E, K> {
    E findById(K id, Class<E> clazz);
    E findOneHasMD5(Class<E> clazz);
    List<E> findByIds(List<K> ids, Class<E> clazz);
    E findByLatestCollection(K collection);
    void drop(String clazzName);
}
