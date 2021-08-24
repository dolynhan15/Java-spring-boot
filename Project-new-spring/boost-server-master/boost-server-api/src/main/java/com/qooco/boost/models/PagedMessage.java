package com.qooco.boost.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class PagedMessage<T> extends PagedResult<T> {
    private boolean isLockedAppointmentBtn;

    public PagedMessage(PagedResult<T> result, boolean isLocked) {
        super(result.getResults(), result.getPage(), result.getSize(), result.getTotalPage(), result.getTotal(), result.isHasNextPage(), result.isHasPreviousPage());
        this.isLockedAppointmentBtn = isLocked;
    }
}
