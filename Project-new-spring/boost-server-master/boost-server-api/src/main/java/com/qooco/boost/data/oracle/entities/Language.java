package com.qooco.boost.data.oracle.entities;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 7/4/2018 - 2:22 PM
 */
@Entity
@Table(name = "LANGUAGE")
public class Language extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LANGUAGE_SEQUENCE")
    @SequenceGenerator(sequenceName = "LANGUAGE_SEQ", allocationSize = 1, name = "LANGUAGE_SEQUENCE")
    @Column(name = "LANGUAGE_ID", nullable = false)
    private Long languageId;

    @Basic(optional = false)
    @Size(min = 1, max = 255)
    @Column(name = "NAME", nullable = false, columnDefinition = "NVARCHAR2")
    private String name;

    @Basic(optional = false)
    @Size(min = 1, max = 255)
    @Column(name = "CODE", nullable = false, columnDefinition = "NVARCHAR2")
    private String code;

    public Language() {
    }

    public Language(Long languageId) {
        this.languageId = languageId;
    }

    public Language(Long languageId, String name, String code) {
        this.languageId = languageId;
        this.name = name;
        this.code = code;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (languageId != null ? languageId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Language language = (Language) o;
        return Objects.equals(languageId, language.languageId);
    }

    @Override
    public String toString() {
        return name;
    }

}
