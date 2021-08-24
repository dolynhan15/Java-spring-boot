package com.qooco.boost.data.oracle.entities.localizations;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class PersonalAssessmentLocalizationId  implements Serializable {

    private String type;
    private Long id;
    private String locale;

    public PersonalAssessmentLocalizationId(Long countryId, String locale, String type) {
        this.id = countryId;
        this.locale = locale;
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getLocale() == null) ? 0 : getLocale().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PersonalAssessmentLocalizationId other = (PersonalAssessmentLocalizationId) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        if (getLocale() == null) {
            if (other.getLocale() != null)
                return false;
        } else if (!getLocale().equals(other.getLocale()))
            return false;
        if (getType() == null) {
            if (other.getType() != null)
                return false;
        } else if (!getType().equals(other.getType()))
            return false;
        return true;
    }
}
