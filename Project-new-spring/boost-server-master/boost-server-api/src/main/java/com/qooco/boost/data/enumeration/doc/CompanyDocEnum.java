package com.qooco.boost.data.enumeration.doc;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 4/25/2019 - 1:43 PM
*/
public enum CompanyDocEnum {

    ID("id"),
    NAME("name"),
    SORT_NAME("sortName"),
    LOGO("logo"),
    ADDRESS("address"),
    PHONE("phone"),
    EMAIL("email"),
    WEB("web"),
    AMADEUS("amadeus"),
    GALILEO("galileo"),
    WORLDSPAN("worldspan"),
    SABRE("sabre"),
    STATUS("status"),
    DESCRIPTION("description"),
    CITY("city"),
    HOTEL_TYPE("hotelType"),
    ADMINS("admins"),
    STAFFS("staffs"),
    UPDATED_DATE("updatedDate");

    private final String key;

    CompanyDocEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}