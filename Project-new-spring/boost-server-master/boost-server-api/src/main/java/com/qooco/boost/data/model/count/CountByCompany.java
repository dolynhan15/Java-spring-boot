package com.qooco.boost.data.model.count;

public class CountByCompany {
    private Long companyId;
    private Long count;

    public CountByCompany() {}

    public CountByCompany(Long companyId, Long count) {
        this.companyId = companyId;
        this.count = count;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
