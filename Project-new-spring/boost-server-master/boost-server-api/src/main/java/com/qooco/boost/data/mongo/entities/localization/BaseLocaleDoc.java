package com.qooco.boost.data.mongo.entities.localization;

import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;

import java.util.Date;

import static com.google.common.base.MoreObjects.firstNonNull;

@FieldNameConstants

public class BaseLocaleDoc {
    @Id
    @Setter
    @Getter
    @Accessors(fluent = true)
    private String id;
    @Setter
    @Accessors(fluent = true)
    private String collection;
    @Setter
    @Getter
    @Accessors(fluent = true)
    private String content;

    @Setter
    private Date updatedTime;
    @Setter
    @Getter
    private String md5;
    private boolean isDeleted;

    public BaseLocaleDoc() {
        this.updatedTime = DateUtils.toServerTimeForMongo();
    }

    @Override
    public String toString() {
        return firstNonNull(content, "");
    }
}
