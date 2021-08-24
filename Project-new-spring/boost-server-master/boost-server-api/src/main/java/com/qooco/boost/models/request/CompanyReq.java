package com.qooco.boost.models.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.utils.ServletUriUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter @NoArgsConstructor
@Builder
@AllArgsConstructor
public class CompanyReq {
    @JsonIgnore
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
    private Long cityId;
    private Long hotelTypeId;

    public Company updateEntity(Company company) {
        if (Objects.isNull(company)) {
            company = new Company();
        }
        if(Objects.isNull(company.getCompanyId())){
            //TODO: Need time when GDS information is remove -> change company's status to pending
//            if (Objects.isNull(company.getAmadeus())
//                    && Objects.isNull(company.getAmadeus())
//                    && Objects.isNull(company.getAmadeus())
//                    && Objects.isNull(company.getAmadeus())){
//                company.setStatus(ApprovalStatus.UPDATED_GSD);
//            }
        }
        company.setCompanyId(id);
        company.setAddress(Objects.nonNull(address)? address.trim() : null);
        company.setCompanyName(Objects.nonNull(name)? name.trim() : null);
        company.setEmail(Objects.nonNull(email)? email.trim() : null);
        company.setDescription(Objects.nonNull(description)? description.trim() : null);

        company.setLogo(ServletUriUtils.getRelativePath(logo.trim()));
        company.setPhone(Objects.nonNull(phone)? phone.trim() : null);
        company.setAmadeus(Objects.nonNull(amadeus)? amadeus.trim().toUpperCase() : null );
        company.setGalileo(Objects.nonNull(galileo)? galileo.trim().toUpperCase() : null);
        company.setWorldspan(Objects.nonNull(worldspan)? worldspan.trim().toUpperCase() : null);

        company.setSabre(Objects.nonNull(sabre)? sabre.trim().toUpperCase() : null);
        company.setWeb(Objects.nonNull(web)? web.trim() : null);
        return company;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyReq that = (CompanyReq) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
