package com.qooco.boost.data.mongo.embedded;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 8/7/2018 - 2:02 PM
*/
public class SoftSkillEmbedded {

    private Long id;
    private String name;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
