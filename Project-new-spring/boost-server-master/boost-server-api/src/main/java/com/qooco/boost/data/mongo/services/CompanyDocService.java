package com.qooco.boost.data.mongo.services;

import com.qooco.boost.data.mongo.embedded.StaffShortEmbedded;
import com.qooco.boost.data.mongo.entities.CompanyDoc;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CompanyDocService extends DocService<CompanyDoc, Long> {

    Page<CompanyDoc> searchFullTextByName(String keyword, int page, int size);

    Page<CompanyDoc> searchFullTextByNameExceptStaff(String keyword, int page, int size, long userId);

    List<CompanyDoc> findNoneStaffCompany(int limit);

    void updateStaffsOfCompanies(List<CompanyDoc> companyDocs);

    void removeStaffInCompany(long companyId, long staffId);

    void addStaffToCompany(long companyId, StaffShortEmbedded staffShortEmbedded);

    void deleteByIds(List<Long> ids);
}
