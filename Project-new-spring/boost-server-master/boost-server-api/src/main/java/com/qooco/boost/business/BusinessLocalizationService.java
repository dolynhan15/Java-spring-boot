package com.qooco.boost.business;

import com.qooco.boost.models.poi.StaticData;

import java.util.List;

public interface BusinessLocalizationService {

    void saveLocalization(List<StaticData> staticData, long createdBy);
}
