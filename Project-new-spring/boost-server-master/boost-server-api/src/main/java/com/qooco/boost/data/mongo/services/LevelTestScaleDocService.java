package com.qooco.boost.data.mongo.services;

import com.qooco.boost.data.mongo.entities.LevelTestScaleDoc;

import java.util.List;

public interface LevelTestScaleDocService extends DocService<LevelTestScaleDoc, String>{

    LevelTestScaleDoc findByLatestLevelTestScale();

    List<LevelTestScaleDoc> findByScaleIds(List<String> scaleIds);

    List<LevelTestScaleDoc> findAll();
}
