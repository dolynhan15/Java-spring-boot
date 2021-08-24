/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "STAFF")
@FieldNameConstants
public class Staff extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Setter @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STAFF_SEQUENCE")
    @SequenceGenerator(sequenceName = "STAFF_SEQ", allocationSize = 1, name = "STAFF_SEQUENCE")
    @Basic(optional = false)
    @Column(name = "STAFF_ID", nullable = false)
    private Long staffId;

    @Setter @Getter
    @JoinColumn(name = "COMPANY_ID", referencedColumnName = "COMPANY_ID")
    @ManyToOne(optional = false)
    private Company company;

    @Setter @Getter
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")
    @ManyToOne(optional = false)
    private RoleCompany role;

    @Setter @Getter
    @JoinColumn(name = "USER_PROFILE_ID", referencedColumnName = "USER_PROFILE_ID")
    @ManyToOne(optional = false)
    private UserFit userFit;

    public Staff() {
        super();
    }

    public Staff(Long staffId) {
        this.staffId = staffId;
    }

    public Staff(Company company, UserFit userFit) {
        this.company = company;
        this.userFit = userFit;
    }

    public Staff(Company company, UserFit userFit, RoleCompany roleCompany) {
        this.company = company;
        this.userFit = userFit;
        this.role = roleCompany;
    }

    public Staff(Long staffId, Company company, UserFit userFit, RoleCompany roleCompany) {
        this.staffId = staffId;
        this.company = company;
        this.userFit = userFit;
        this.role = roleCompany;
    }

    public Staff(Staff contactPerson) {
        if (Objects.nonNull(contactPerson)) {
            staffId = contactPerson.getStaffId();
            if (Objects.nonNull(contactPerson.getCompany())) {
                company = new Company(contactPerson.getCompany());
            }
            if (Objects.nonNull(contactPerson.getUserFit())) {
                userFit = new UserFit(contactPerson.getUserFit());
            }
            if (Objects.nonNull(contactPerson.getCompany())) {
                role = new RoleCompany(contactPerson.getRole());
            }
        }
    }

    public Staff(Long staffId, Company company) {
        this.staffId = staffId;
        if (Objects.nonNull(company)) {
            this.company = company;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Staff staff = (Staff) o;
        return Objects.equals(staffId, staff.staffId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(staffId);
    }

    @Override
    public String toString() {
        return Objects.nonNull(userFit) ? userFit.getUsername() : "";
    }
    
}
