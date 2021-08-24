package com.qooco.boost.models.qooco.sync.levelTestScales;

import java.util.Map;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/9/2018 - 2:46 PM
*/
public class LevelTestScale {

    private String mappingID;
    private Map<String, Level> levels;

    public String getMappingID() {
        return mappingID;
    }

    public void setMappingID(String mappingID) {
        this.mappingID = mappingID;
    }

    public Map<String, Level> getLevels() {
        return levels;
    }

    public void setLevels(Map<String, Level> levels) {
        this.levels = levels;
    }
}
