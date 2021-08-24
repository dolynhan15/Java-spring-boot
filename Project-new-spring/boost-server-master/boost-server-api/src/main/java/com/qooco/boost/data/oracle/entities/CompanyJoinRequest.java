package com.qooco.boost.data.oracle.entities;

import com.qooco.boost.data.enumeration.JoinCompanyStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "COMPANY_JOIN_REQUEST")
public class CompanyJoinRequest extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPANY_JOIN_REQUEST_SEQUENCE")
    @SequenceGenerator(sequenceName = "COMPANY_JOIN_REQUEST_SEQ", allocationSize = 1, name = "COMPANY_JOIN_REQUEST_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "COMPANY_JOIN_REQUEST_ID")
    private Long companyJoinRequestId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "STATUS")
    private JoinCompanyStatus status;

    @JoinColumn(name = "COMPANY_ID", referencedColumnName = "COMPANY_ID")
    @ManyToOne(optional = false)
    private Company company;

    @JoinColumn(name = "USER_PROFILE_ID", referencedColumnName = "USER_PROFILE_ID")
    @ManyToOne(optional = false)
    private UserFit userFit;

    public CompanyJoinRequest() {
        super();
    }

    public CompanyJoinRequest(Long companyJoinRequestId) {
        this.companyJoinRequestId = companyJoinRequestId;
    }

    public CompanyJoinRequest(JoinCompanyStatus status, Company company, UserFit userFit) {
        super(userFit.getUserProfileId());
        this.status = status;
        this.company = company;
        this.userFit = userFit;
    }

    public CompanyJoinRequest(Long id, Date createdDate, Company company) {
        this.companyJoinRequestId = id;
        this.setCreatedDate(createdDate);
        this.company = company;
    }

    public CompanyJoinRequest(Long id, Company company) {
        this.companyJoinRequestId = id;
        if (Objects.nonNull(company)) {
            this.company = company;
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (companyJoinRequestId != null ? companyJoinRequestId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyJoinRequest that = (CompanyJoinRequest) o;
        return Objects.equals(companyJoinRequestId, that.companyJoinRequestId);
    }

    @Override
    public String toString() {
        return userFit.getUsername();
    }

}