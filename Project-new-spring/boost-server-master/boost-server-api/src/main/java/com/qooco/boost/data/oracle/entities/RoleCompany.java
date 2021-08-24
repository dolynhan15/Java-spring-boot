package com.qooco.boost.data.oracle.entities;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 7/3/2018 - 1:41 PM
 */
@Entity
@Table(name = "ROLE_COMPANY")
public class RoleCompany extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROLE_SEQUENCE")
    @SequenceGenerator(sequenceName = "ROLE_SEQ", allocationSize = 1, name = "ROLE_SEQUENCE")
    @Basic(optional = false)
    @Column(name = "ROLE_ID", nullable = false)
    private Long roleId;

    @Basic(optional = false)
    @Size(min = 1, max = 255)
    @Column(name = "NAME", columnDefinition = "NVARCHAR2", nullable = false)
    private String name;

    @Basic(optional = false)
    @Size(min = 1, max = 255)
    @Column(name = "DISPLAY_NAME", columnDefinition = "NVARCHAR2", nullable = false)
    private String displayName;

    @ManyToMany(mappedBy = "roleCompanyList")
    private List<Permission> permissionList;

    public RoleCompany() {
        super();
    }

    public RoleCompany(Long roleId) {
        super();
        this.roleId = roleId;
    }

    public RoleCompany(Long roleId, String name) {
        super();
        this.roleId = roleId;
        this.name = name;
    }

    public RoleCompany(Long roleId, String name, Long createdBy, Date createdDate, Long updatedBy, Date updatedDate) {
        super(createdDate, createdBy, updatedDate, updatedBy, false);
        this.roleId = roleId;
        this.name = name;
    }

    public RoleCompany(Long roleId, String name, String displayName) {
        super();
        this.roleId = roleId;
        this.name = name;
        this.displayName = displayName;
    }

    public RoleCompany(RoleCompany role) {
        if (Objects.nonNull(role)) {
            roleId = role.getRoleId();
            name = role.getName();
            displayName = role.getDisplayName();
            if (CollectionUtils.isNotEmpty(permissionList)) {
                permissionList = Lists.newArrayList(permissionList);
            }
        }
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @XmlTransient
    public List<Permission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<Permission> permissionList) {
        this.permissionList = permissionList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleCompany that = (RoleCompany) o;
        return Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId);
    }

    @Override
    public String toString() {
        return name;
    }
}