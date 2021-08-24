package com.qooco.boost.models.dto.company;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.models.dto.CityDTO;
import com.qooco.boost.models.dto.HotelTypeDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyWorkedDTO extends CompanyBaseDTO {
    private HotelTypeDTO hotelType;
    private CityDTO city;
    private int type;

    public CompanyWorkedDTO(@NotNull Company company, String locale) {
        super(company);
        if (Objects.nonNull(company.getHotelType())) {
            this.hotelType = new HotelTypeDTO(company.getHotelType(), locale);
        }
        if (Objects.nonNull(company.getCity())) {
            this.city = new CityDTO(company.getCity(), locale);
        }
    }
}
