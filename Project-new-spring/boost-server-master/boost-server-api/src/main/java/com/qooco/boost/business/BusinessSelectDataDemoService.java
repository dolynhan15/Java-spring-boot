package com.qooco.boost.business;


import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.demo.SelectDataReq;

public interface BusinessSelectDataDemoService extends BaseBusinessService {
    BaseResp genSelectDataDemo(SelectDataReq req);

    BaseResp deleteSelectDataDemo(Long companyId);
}
