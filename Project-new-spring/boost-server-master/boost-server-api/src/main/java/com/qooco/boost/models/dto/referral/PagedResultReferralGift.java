package com.qooco.boost.models.dto.referral;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.models.PagedResult;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 12/11/2018 - 10:12 AM
*/
@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagedResultReferralGift<T> extends PagedResult<T> {

    private int inActiveGift;

    public PagedResultReferralGift(List<T> results, int page, int size, int totalPage, long total, boolean hasNext, boolean hasPrevious) {
        super(results, page, size, totalPage, total, hasNext, hasPrevious);
    }
}
