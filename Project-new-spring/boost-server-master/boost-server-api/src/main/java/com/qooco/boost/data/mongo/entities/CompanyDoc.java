package com.qooco.boost.data.mongo.entities;

import com.qooco.boost.data.mongo.embedded.CityEmbedded;
import com.qooco.boost.data.mongo.embedded.HotelTypeEmbedded;
import com.qooco.boost.data.mongo.embedded.StaffShortEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Document(collection = "CompanyDoc")
@FieldNameConstants
public class CompanyDoc {

    @Id
    @Setter @Getter
    private Long id;
    @TextIndexed
    @Getter
    private String name;
    @Setter @Getter
    private String sortName;
    @Setter @Getter
    private String logo;
    @Setter @Getter
    private String address;
    @Setter @Getter
    private String phone;
    @Setter @Getter
    private String email;
    @Setter @Getter
    private String web;
    @Setter @Getter
    private String amadeus;
    @Setter @Getter
    private String galileo;
    @Setter @Getter
    private String worldspan;
    @Setter @Getter
    private String sabre;
    @Setter @Getter
    private String description;
    @Setter @Getter
    private int status;
    @Setter @Getter
    private CityEmbedded city;
    @Setter @Getter
    private HotelTypeEmbedded hotelType;
    @Setter @Getter
    private Date updatedDate;

    @Setter @Getter
    private List<UserProfileEmbedded> admins;
    @Setter @Getter
    private List<StaffShortEmbedded> staffs;

    public CompanyDoc(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
        if (Objects.nonNull(name)) {
            this.sortName = name.toLowerCase();
        }
    }
}