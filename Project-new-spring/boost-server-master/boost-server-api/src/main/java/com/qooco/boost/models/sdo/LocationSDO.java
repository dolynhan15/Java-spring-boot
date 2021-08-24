package com.qooco.boost.models.sdo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter @Getter @NoArgsConstructor
public class LocationSDO {
    private Long cityId;
    private Long companyId;

    public LocationSDO(Long cityId, Long companyId) {
        this.cityId = cityId;
        this.companyId = companyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationSDO that = (LocationSDO) o;
        return Objects.equals(cityId, that.cityId) &&
                Objects.equals(companyId, that.companyId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(cityId, companyId);
    }
}
