/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qooco.boost.data.oracle.entities;

import com.qooco.boost.data.enumeration.ApprovalStatus;
import com.qooco.boost.data.mongo.embedded.CompanyEmbedded;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Getter @Setter
@Table(name = "COMPANY")
@FieldNameConstants
public class Company extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPANY_SEQUENCE")
    @SequenceGenerator(sequenceName = "COMPANY_SEQ", allocationSize = 1, name = "COMPANY_SEQUENCE")
    @Basic(optional = false)
    @Column(name = "COMPANY_ID", nullable = false)
    private Long companyId;

    @Basic(optional = false)
    @Size(min = 1, max = 50)
    @Column(name = "COMPANY_NAME", nullable = false, columnDefinition = "NVARCHAR2")
    private String companyName;

    @Basic(optional = false)
    @Size(min = 1, max = 2000)
    @Column(name = "LOGO", nullable = false, columnDefinition = "VARCHAR")
    private String logo;

    @Basic(optional = false)
    @Size(max = 50)
    @Column(name = "ADDRESS", nullable = false, columnDefinition = "NVARCHAR2")
    private String address;

    @Basic(optional = false)
    @Size(min = 1, max = 15)
    @Column(name = "PHONE", nullable = false, columnDefinition = "VARCHAR")
    private String phone;

    @Basic(optional = false)
    @Size(min = 1, max = 255)
    @Column(name = "EMAIL", nullable = false, columnDefinition = "NVARCHAR2")
    private String email;

    @Basic(optional = false)
    @Size(max = 100)
    @Column(name = "WEB", nullable = false, columnDefinition = "VARCHAR")
    private String web;

    @Size(max = 10)
    @Column(name = "AMADEUS",nullable = false, columnDefinition = "VARCHAR")
    private String amadeus;

    @Size(max = 10)
    @Column(name = "GALILEO",nullable = false, columnDefinition = "VARCHAR")
    private String galileo;

    @Size(max = 10)
    @Column(name = "WORLDSPAN",nullable = false, columnDefinition = "VARCHAR")
    private String worldspan;

    @Size(max = 10)
    @Column(name = "SABRE",nullable = false, columnDefinition = "VARCHAR")
    private String sabre;

    @Size(max = 700)
    @Column(name = "DESCRIPTION", nullable = false, columnDefinition = "NVARCHAR2(700)")
    private String description;

    @Column(name = "STATUS", nullable = false)
    private ApprovalStatus status;

    @JoinColumn(name = "CITY_ID", referencedColumnName = "CITY_ID")
    @ManyToOne(optional = false)
    private City city;

    @JoinColumn(name = "HOTEL_TYPE_ID", referencedColumnName = "HOTEL_TYPE_ID")
    @ManyToOne()
    private HotelType hotelType;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.EXTRA)
    @Where(clause = "IS_PRIMARY > 0 ")
    private List<Location> locations;

    public Company() {
        super();
    }

    public Company(Long companyId) {
        this.companyId = companyId;
    }

    public Company(Long companyId, ApprovalStatus status) {
        this.companyId = companyId;
        this.status = status;
    }

    public Company(Long companyId, String companyName, String logo, String address, String phone, String email, String web, String sabre, ApprovalStatus status) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.logo = logo;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.web = web;
        this.sabre = sabre;
        this.status = status;
    }

    public Company(Company defaultCompany) {
        if (Objects.nonNull(defaultCompany)) {
            companyId = defaultCompany.getCompanyId();
            companyName = defaultCompany.getCompanyName();
            logo = defaultCompany.getLogo();
            address =  defaultCompany.getAddress();
            phone = defaultCompany.getPhone();
            email = defaultCompany.getEmail();
            web = defaultCompany.getWeb();
            amadeus = defaultCompany.getAmadeus();
            galileo = defaultCompany.getGalileo();
            worldspan = defaultCompany.getWorldspan();
            sabre = defaultCompany.getSabre();
            description = defaultCompany.getDescription();
            status = defaultCompany.getStatus();
            setUpdatedBy(defaultCompany.getUpdatedBy());
            setCreatedBy(defaultCompany.getCreatedBy());
            setUpdatedDate(defaultCompany.getUpdatedDate());
            setCreatedDate(defaultCompany.getCreatedDate());
            if (Objects.nonNull(defaultCompany.getCity())) {
                city = new City(city);
            }
            if (Objects.nonNull(defaultCompany.getHotelType())) {
                hotelType = new HotelType();
            }
        }
    }

    public Company(CompanyEmbedded defaultCompany) {
        if (Objects.nonNull(defaultCompany)) {
            companyId = defaultCompany.getId();
            companyName = defaultCompany.getName();
            logo = defaultCompany.getLogo();
            address =  defaultCompany.getAddress();
            phone = defaultCompany.getPhone();
            email = defaultCompany.getEmail();
            web = defaultCompany.getWeb();
            amadeus = defaultCompany.getAmadeus();
            galileo = defaultCompany.getGalileo();
            worldspan = defaultCompany.getWorldspan();
            sabre = defaultCompany.getSabre();
        }
    }

    public Company(Long companyId, Date createdDate) {
        this.companyId = companyId;
        this.setCreatedDate(createdDate);
    }

    public Company(Long companyId, String companyName) {
        this.companyId = companyId;
        this.companyName = companyName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (companyId != null ? companyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(companyId, company.companyId);
    }

    @Override
    public String toString() {
        return companyName;
    }

}
