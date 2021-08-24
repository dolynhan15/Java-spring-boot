package com.qooco.boost.threads.notifications.dto;

import com.qooco.boost.data.mongo.embedded.CompanyEmbedded;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.utils.ServletUriUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor
public class CompanyDTO {
    private Long id;
    private String name;
    private String logo;
    private String address;
    private String phone;

    public CompanyDTO(CompanyEmbedded company) {
        this.id = company.getId();
        this.name = company.getName();
        this.logo = ServletUriUtils.getAbsolutePath(company.getLogo());
        this.address = company.getAddress();
        this.phone = company.getPhone();
    }

    public CompanyDTO(Company company) {
        this.id = company.getCompanyId();
        this.name = company.getCompanyName();
        this.logo = ServletUriUtils.getAbsolutePath(company.getLogo());
        this.address = company.getAddress();
        this.phone = company.getPhone();
    }
}
