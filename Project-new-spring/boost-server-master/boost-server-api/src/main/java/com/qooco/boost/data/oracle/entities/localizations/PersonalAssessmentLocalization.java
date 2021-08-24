package com.qooco.boost.data.oracle.entities.localizations;

import com.qooco.boost.data.oracle.entities.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/*
 * Copyright: Falcon Team - AxonActive
 * User: nhphuc
 * Date: 7/11/2018 - 9:22 PM
 */
@Entity
@Getter
@Setter
@Table(name = "PA_LOCALIZATION")
@FieldNameConstants
@IdClass(PersonalAssessmentLocalizationId.class)
@NoArgsConstructor
@ToString
public class PersonalAssessmentLocalization extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    private Long id;

    @Id
    @Column(name = "LOCALE")
    private String locale;

    @Id
    @Column(name = "TYPE")
    private String type;

    @Lob
    @Column(name = "CONTENT")
    private String content;

    public PersonalAssessmentLocalization(long provinceId, String locale, String content, String type, Long createdBy) {
        super(createdBy);
        this.id = provinceId;
        this.locale = locale;
        this.content = content;
        this.type = type;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        hash += (locale != null ? locale.hashCode() : 0);
        hash += (type != null ? type.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonalAssessmentLocalization pa = (PersonalAssessmentLocalization) o;
        return Objects.equals(id, pa.id) && Objects.equals(locale, pa.getLocale()) && Objects.equals(type, pa.getType());
    }

}
