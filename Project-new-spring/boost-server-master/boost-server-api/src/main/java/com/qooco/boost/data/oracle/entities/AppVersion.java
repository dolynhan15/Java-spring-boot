package com.qooco.boost.data.oracle.entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 9/6/2018 - 11:17 AM
*/
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "APP_VERSION")
public class AppVersion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APP_VERSION_SEQUENCE")
    @SequenceGenerator(sequenceName = "APP_VERSION_SEQ", allocationSize = 1, name = "APP_VERSION_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "APP_VERSION_ID")
    private Long id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "APP_ID")
    private String appId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "APP_VERSION")
    private Integer appVersion;

    @NotNull
    @Column(name = "APP_VERSION_NAME")
    private String appVersionName;

    @Basic(optional = false)
    @NotNull
    @Column(name = "OS")
    private String os;


    @Basic(optional = false)
    @Column(name = "IS_FORCE_UPDATE")
    private boolean isForceUpdate;

    @Basic(optional = false)
    @Column(name = "UPDATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @Basic(optional = false)
    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;


    public AppVersion(Long id) {
        this.id = id;
    }
}
