package com.qooco.boost.data.mongo.embedded;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 8/7/2018 - 1:57 PM
*/
public class BenefitEmbedded {

    private Long id;
    private String description;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
