package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.ShareCode;
import com.qooco.boost.data.oracle.reposistories.ShareCodeRepository;
import com.qooco.boost.data.oracle.services.ShareCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ShareCodeServiceImpl implements ShareCodeService {
    @Autowired
    private ShareCodeRepository repository;

    @Override
    public ShareCode save(ShareCode shareCode) {
        return repository.save(shareCode);
    }

    @Override
    public List<Object[]> findUserSharedCodeGroupByLocationAndDuration(Date startDate, Date endDate) {
        return repository.findUserSharedCodeGroupByLocationAndDuration(startDate, endDate);
    }
}
