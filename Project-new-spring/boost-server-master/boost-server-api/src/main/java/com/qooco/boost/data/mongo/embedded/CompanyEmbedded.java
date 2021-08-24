package com.qooco.boost.data.mongo.embedded;

import com.qooco.boost.data.oracle.entities.Company;
import lombok.*;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyEmbedded {
    private Long id;
    private String name;
    private String logo;
    private String address;
    private String phone;
    private String email;
    private String web;
    private String amadeus;
    private String galileo;
    private String worldspan;
    private String sabre;
    private String description;
    private CityEmbedded city;
    private HotelTypeEmbedded hotelType;

    public CompanyEmbedded(Long id) {
        this.id = id;
    }

    public CompanyEmbedded(CompanyEmbedded embedded) {
        if (Objects.nonNull(embedded)) {
            id = embedded.getId();
            name = embedded.getName();
            logo = embedded.getLogo();
            address = embedded.getAddress();
            phone = embedded.getPhone();
            email = embedded.getEmail();
            web = embedded.getWeb();
            amadeus = embedded.getAmadeus();
            galileo = embedded.getGalileo();
            worldspan = embedded.getWorldspan();
            sabre = embedded.getSabre();
            description = embedded.getDescription();
            city = Objects.nonNull(embedded.getCity()) && Objects.nonNull(embedded.getCity().getId()) ? embedded.getCity() : null;
            hotelType = Objects.nonNull(embedded.getHotelType()) && Objects.nonNull(embedded.getHotelType().getId()) ? embedded.getHotelType() : null;
        }
    }

    public CompanyEmbedded(Company company) {
        if (Objects.nonNull(company)) {
            id = company.getCompanyId();
            name = company.getCompanyName();
            logo = company.getLogo();
            address = company.getAddress();
            phone = company.getPhone();
            email = company.getEmail();
            web = company.getWeb();
            amadeus = company.getAmadeus();
            galileo = company.getGalileo();
            worldspan = company.getWorldspan();
            sabre = company.getSabre();
            description = company.getDescription();
            city = Objects.nonNull(company.getCity()) && Objects.nonNull(company.getCity().getCityId()) ? new CityEmbedded(company.getCity()) : null;
            hotelType = Objects.nonNull(company.getHotelType()) && Objects.nonNull(company.getHotelType().getHotelTypeId())
                    ? new HotelTypeEmbedded(company.getHotelType()) : null;
        }
    }
}
