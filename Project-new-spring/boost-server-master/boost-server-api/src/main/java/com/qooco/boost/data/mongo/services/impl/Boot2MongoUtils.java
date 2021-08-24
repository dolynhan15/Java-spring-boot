package com.qooco.boost.data.mongo.services.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.util.Map;

import static org.springframework.boot.SpringBootVersion.getVersion;

public class Boot2MongoUtils {
    public static <T extends Map<String, Object>, Serializable, Bson> T newQueryObject(final String key, final Object value) {
        return getVersion().startsWith("1.") ? (T) new BasicDBObject(key, value) : (T) new Document(key, value);
    }

    public static boolean wasAcknowledged(BulkWriteResult bulkWriteResult) {
        return bulkWriteResult.wasAcknowledged();
    }

    public static boolean wasAcknowledged(com.mongodb.BulkWriteResult bulkWriteResult) {
        return bulkWriteResult.isAcknowledged();
    }

    public static int getModifiedCount(WriteResult writeResult) {
        return writeResult.getN();
    }

    public static long getModifiedCount(UpdateResult updateResult) {
        return updateResult.getModifiedCount();
    }

    public static WriteResult toWriteResult(WriteResult writeResult) {
        return writeResult;
    }

    public static WriteResult toWriteResult(UpdateResult updateResult) {
        return new WriteResult((int) updateResult.getModifiedCount(), true, updateResult.getUpsertedId());
    }
}
