package com.qooco.boost.data.mongo.services.impl.localization.boost;

import com.qooco.boost.data.mongo.entities.localization.BaseLocaleDoc;
import com.qooco.boost.data.mongo.entities.localization.boost.BoostEnUsDoc;
import com.qooco.boost.data.mongo.services.impl.localization.BaseLocaleAbstract;
import com.qooco.boost.data.mongo.services.localization.boost.BoostLocaleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoostLocaleServiceImpl extends BaseLocaleAbstract implements BoostLocaleService {

    @Override
    public BaseLocaleDoc findById(String id, Class<?> clazz) {
        return super.findById(id, clazz);
    }

    @Override
    public BaseLocaleDoc findById(String id) {
        return super.findById(id, BoostEnUsDoc.class);
    }

    @Override
    public BaseLocaleDoc findOneHasMD5(Class clazz) {
        return super.findOneHasMD5(clazz);
    }

    @Override
    public List<BaseLocaleDoc> findById(List<String> ids, Class<?> clazz) {
        return super.findByIds(ids, clazz);
    }

    @Override
    public List<BaseLocaleDoc> findById(List<String> ids) {
        return super.findByIds(ids, BoostEnUsDoc.class);
    }

    @Override
    public void insertAll(List docs, Class clazz) {
        super.insertAll(docs, clazz);
    }
}
