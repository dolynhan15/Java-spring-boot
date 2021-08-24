package com.qooco.boost.models.request;

import com.qooco.boost.data.oracle.entities.Vacancy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/6/2018 - 11:26 AM
 */

@Setter @Getter @NoArgsConstructor
public class VacancyReq extends VacancyBaseReq{
    private Long id;

    public Vacancy updateEntity(Vacancy vacancy, Long updatedOwner){
        vacancy = super.updateEntity(vacancy, updatedOwner);
        vacancy.setId(this.id);
        return vacancy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VacancyReq that = (VacancyReq) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
