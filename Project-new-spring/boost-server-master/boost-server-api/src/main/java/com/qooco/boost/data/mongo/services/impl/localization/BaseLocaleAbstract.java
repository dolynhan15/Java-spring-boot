package com.qooco.boost.data.mongo.services.impl.localization;

import com.qooco.boost.data.mongo.entities.localization.BaseLocaleDoc;
import com.qooco.boost.data.mongo.services.localization.BaseLocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public abstract class BaseLocaleAbstract<E extends BaseLocaleDoc, K> implements BaseLocaleService<E, K> {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public E findById(K id, Class<E> clazz) {
        return mongoTemplate.findById(id, clazz);
    }

    @Override
    public List<E> findByIds(List<K> ids, Class<E> clazz) {
        Criteria criteria = Criteria.where(BaseLocaleDoc.Fields.id).in(ids);
        return mongoTemplate.find(new Query(criteria), clazz);
    }


    @Override
    public E findOneHasMD5(Class<E> clazz) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where(BaseLocaleDoc.Fields.md5).exists(true),
                Criteria.where(BaseLocaleDoc.Fields.isDeleted).is(false)
        );
        return mongoTemplate.findOne(new Query(criteria), clazz);
    }


    @Override
    public E findByLatestCollection(K collection) {
        return null;
    }

    public void drop(String clazzName) {
        mongoTemplate.dropCollection(clazzName);
    }

    protected void insertAll(List<E> docs, Class<E> clazz) {

        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, clazz);
        docs.forEach(doc -> {
            bulkOps.remove(new Query(Criteria.where(BaseLocaleDoc.Fields.id).is(doc.id())));
            bulkOps.insert(doc);
        });
        bulkOps.execute();
    }
}
