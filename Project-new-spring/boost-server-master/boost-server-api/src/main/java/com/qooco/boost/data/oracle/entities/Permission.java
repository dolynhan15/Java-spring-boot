package com.qooco.boost.data.oracle.entities;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 7/3/2018 - 1:43 PM
 */
@Entity
@Table(name = "PERMISSION")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERMISSION_SEQUENCE")
    @SequenceGenerator(sequenceName = "PERMISSION_SEQ", allocationSize = 1, name = "PERMISSION_SEQUENCE")
    @Basic(optional = false)
    @Column(name = "PERMISSION_ID", nullable = false)
    private Long permissionId;

    @Basic(optional = false)
    @Size(min = 1, max = 255)
    @Column(name = "NAME", nullable = false)
    private String name;

    @Basic(optional = false)
    @Size(min = 1, max = 255)
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @JoinTable(name = "ROLE_PERMISSION", joinColumns = {
            @JoinColumn(name = "PERMISSION_ID", referencedColumnName = "PERMISSION_ID")}, inverseJoinColumns = {
            @JoinColumn(name = "COMPANY_ROLE_ID", referencedColumnName = "ROLE_ID")})
    @ManyToMany
    private List<RoleCompany> roleCompanyList;

    public Permission() {
    }

    public Permission(Long permissionId) {
        this.permissionId = permissionId;
    }

    public Permission(Long permissionId, String name, String description) {
        this.permissionId = permissionId;
        this.name = name;
        this.description = description;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<RoleCompany> getRoleCompanyList() {
        return roleCompanyList;
    }

    public void setRoleCompanyList(List<RoleCompany> roleCompanyList) {
        this.roleCompanyList = roleCompanyList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (permissionId != null ? permissionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(permissionId, that.permissionId);
    }

    @Override
    public String toString() {
        return name;
    }

}
