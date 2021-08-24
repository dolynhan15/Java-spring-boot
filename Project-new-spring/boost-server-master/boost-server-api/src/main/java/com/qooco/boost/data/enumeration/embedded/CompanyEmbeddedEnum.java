package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.CompanyEmbedded;

public enum CompanyEmbeddedEnum {
    ID("id"),
    NAME("name"),
    LOGO("logo"),
    ADDRESS("address"),
    PHONE("phone"),
    EMAIL("email"),
    WEB("web"),
    AMADEUS("amadeus"),
    GALILEO("galileo"),
    WORLDSPAN("worldspan"),
    SABRE("sabre");
    private final String key;

    public String getKey() {
        return key;
    }

    public Object getValue(CompanyEmbedded embedded) {
        switch (this) {
            case ID:
                return embedded.getId();
            case LOGO:
                return embedded.getLogo();
            case NAME:
                return embedded.getName();
            case ADDRESS:
                return embedded.getAddress();
            case PHONE:
                return embedded.getPhone();
            case EMAIL:
                return embedded.getEmail();
            case WEB:
                return embedded.getWeb();
            case AMADEUS:
                return embedded.getAmadeus();
            case GALILEO:
                return embedded.getGalileo();
            case WORLDSPAN:
                return embedded.getWorldspan();
            case SABRE:
                return embedded.getSabre();
        }
        return null;
    }

    CompanyEmbeddedEnum(String key) {
        this.key = key;
    }
}
