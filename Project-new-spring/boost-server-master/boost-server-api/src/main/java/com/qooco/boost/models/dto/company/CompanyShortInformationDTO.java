package com.qooco.boost.models.dto.company;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.CompanyEmbedded;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.models.dto.CityDTO;
import com.qooco.boost.models.dto.HotelTypeDTO;
import com.qooco.boost.models.dto.user.ShortUserDTO;
import com.qooco.boost.utils.ServletUriUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyShortInformationDTO extends CompanyBaseDTO {
    private String logo;
    private String description;
    private String address;
    private String phone;
    private HotelTypeDTO hotelType;
    private CityDTO city;
    private ShortUserDTO contactPerson;

    public CompanyShortInformationDTO(Company company, String locale) {
        if (Objects.nonNull(company)) {
            setId(company.getCompanyId());
            setName(company.getCompanyName());
            setLogo(ServletUriUtils.getAbsolutePath(company.getLogo()));
            setDescription(company.getDescription());
            setPhone(company.getPhone());
            setAddress(company.getAddress());
            this.city = Objects.nonNull(company.getCity()) && Objects.nonNull(company.getCity().getCityId()) ?
                    new CityDTO(company.getCity(), locale) : null;
            this.hotelType = Objects.nonNull(company.getHotelType()) && Objects.nonNull(company.getHotelType().getHotelTypeId()) ?
                    new HotelTypeDTO(company.getHotelType(), locale) : null;
        }
    }

    public CompanyShortInformationDTO(CompanyEmbedded company) {
        if (company != null) {
            setId(company.getId());
            setName(company.getName());
            setLogo(ServletUriUtils.getAbsolutePath(company.getLogo()));
        }
    }
}
