package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.ShareCode;

import java.util.Date;
import java.util.List;

public interface ShareCodeService {
    ShareCode save(ShareCode shareCode);
    List<Object[]> findUserSharedCodeGroupByLocationAndDuration(Date startDate, Date endDate);
}
