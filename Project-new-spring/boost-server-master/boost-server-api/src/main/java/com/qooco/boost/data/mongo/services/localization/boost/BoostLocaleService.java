package com.qooco.boost.data.mongo.services.localization.boost;

import com.qooco.boost.data.mongo.entities.localization.BaseLocaleDoc;

import java.util.List;

public interface BoostLocaleService {
    BaseLocaleDoc findById(String id);
    BaseLocaleDoc findById(String id, Class<?> clazz);
    BaseLocaleDoc findOneHasMD5(Class<?> clazz);
    List<BaseLocaleDoc> findById(List<String> ids);
    List<BaseLocaleDoc> findById(List<String> ids, Class<?> clazz);
    void insertAll(List<? extends BaseLocaleDoc> docs, Class<?> clazz);
    void drop(String clazzName);
}
