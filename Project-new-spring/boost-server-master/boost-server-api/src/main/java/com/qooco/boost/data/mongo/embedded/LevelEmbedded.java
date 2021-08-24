package com.qooco.boost.data.mongo.embedded;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/9/2018 - 3:00 PM
*/
@Getter
@Setter
@NoArgsConstructor
public class LevelEmbedded {

    private String value;
    private String name;
    private String descr;

    public LevelEmbedded(String value, String name, String descr) {
        this.value = value;
        this.name = name;
        this.descr= descr;
    }
}
