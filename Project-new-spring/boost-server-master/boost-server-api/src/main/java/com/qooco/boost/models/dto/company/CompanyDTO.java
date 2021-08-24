package com.qooco.boost.models.dto.company;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.enumeration.ApprovalStatus;
import com.qooco.boost.data.mongo.embedded.CompanyEmbedded;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.models.dto.CityDTO;
import com.qooco.boost.models.dto.HotelTypeDTO;
import com.qooco.boost.utils.ServletUriUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyDTO extends CompanyShortInformationDTO {
    private String email;
    private String web;
    private String amadeus;
    private String galileo;
    private String worldspan;
    private String sabre;
    private ApprovalStatus status;

    public CompanyDTO(String address, String phone, String email, String web, String amadeus, String galileo,
                      String worldspan, String sabre, String fullDescription, ApprovalStatus status, CityDTO city,
                      HotelTypeDTO hotelType) {
        setAddress(address);
        setPhone(phone);
        this.email = email;
        this.web = web;
        this.amadeus = amadeus;
        this.galileo = galileo;
        this.worldspan = worldspan;
        this.sabre = sabre;
        this.status = status;
        this.setDescription(fullDescription);
        setCity(Objects.nonNull(city) ? city : null);
        setHotelType(Objects.nonNull(hotelType) ? hotelType : null);
    }

    public CompanyDTO(Company company, String locale) {
        if (Objects.nonNull(company)) {
            this.setId(company.getCompanyId());
            this.setName(company.getCompanyName());
            this.setLogo(ServletUriUtils.getAbsolutePath(company.getLogo()));
            this.setDescription(company.getDescription());
            this.email = company.getEmail();
            this.web = company.getWeb();
            this.amadeus = company.getAmadeus();
            this.galileo = company.getGalileo();
            this.worldspan = company.getWorldspan();
            this.sabre = company.getSabre();
            this.status = company.getStatus();
            this.setAddress(company.getAddress());
            this.setPhone(company.getPhone());
            this.setHotelType(Objects.nonNull(company.getHotelType()) ? new HotelTypeDTO(company.getHotelType(), locale) : null);
            this.setCity(Objects.nonNull(company.getCity()) ? new CityDTO(company.getCity(), locale) : null);
        }
    }

    public CompanyDTO(CompanyEmbedded company) {
        if (Objects.nonNull(company)) {
            this.setId(company.getId());
            this.setName(company.getName());
            this.setLogo(ServletUriUtils.getAbsolutePath(company.getLogo()));
            this.email = company.getEmail();
            this.web = company.getWeb();
            this.amadeus = company.getAmadeus();
            this.galileo = company.getGalileo();
            this.worldspan = company.getWorldspan();
            this.sabre = company.getSabre();
        }
    }

    public CompanyDTO(Long id) {
        this.setId(id);
    }
}
