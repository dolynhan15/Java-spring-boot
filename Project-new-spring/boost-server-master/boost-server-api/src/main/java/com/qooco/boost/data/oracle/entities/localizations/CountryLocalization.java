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
@Table(name = "COUNTRY_LOCALIZATION")
@FieldNameConstants
@IdClass(LocalizationId.class)
@NoArgsConstructor
@ToString
public class CountryLocalization extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    private Long id;

    @Id
    @Column(name = "LOCALE")
    private String locale;

    @Lob
    @Column(name = "CONTENT")
    private String content;

    public CountryLocalization(long countryId, String locale, String content, Long createdBy) {
        super(createdBy);
        this.id = countryId;
        this.locale = locale;
        this.content = content;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountryLocalization country = (CountryLocalization) o;
        return Objects.equals(id, country.id) && Objects.equals(locale, country.getLocale());
    }

}
