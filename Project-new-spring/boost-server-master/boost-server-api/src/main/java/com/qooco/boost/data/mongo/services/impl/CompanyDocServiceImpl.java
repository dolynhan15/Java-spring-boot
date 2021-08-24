package com.qooco.boost.data.mongo.services.impl;

import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.enumeration.doc.CompanyDocEnum;
import com.qooco.boost.data.enumeration.embedded.StaffShortEmbeddedEnum;
import com.qooco.boost.data.enumeration.embedded.UserProfileEmbeddedEnum;
import com.qooco.boost.data.mongo.embedded.StaffShortEmbedded;
import com.qooco.boost.data.mongo.entities.CompanyDoc;
import com.qooco.boost.data.mongo.repositories.CompanyDocRepository;
import com.qooco.boost.data.mongo.services.CompanyDocService;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class CompanyDocServiceImpl implements CompanyDocService {

    @Autowired
    private CompanyDocRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public MongoRepository getRepository() {
        return repository;
    }

    public void deleteByIds(List<Long> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            Criteria criteria = Criteria.where(CompanyDocEnum.ID.getKey()).in(ids);
            Query query = new Query(criteria);
            mongoTemplate.remove(query, CompanyDoc.class);
        }
    }

    @Override
    public void addStaffToCompany(long companyId, StaffShortEmbedded staff) {
        if (Objects.nonNull(staff)) {
            Criteria criteria = Criteria.where(CompanyDocEnum.ID.getKey()).is(companyId);
            Query query = new Query(criteria);
            Update update = new Update();
            update.push(CompanyDocEnum.STAFFS.getKey(), staff);
            mongoTemplate.updateFirst(query, update, CompanyDoc.class);
        }
    }

    private Query initSearchFullTextByName(String keyword, int page, int size) {
        final Pageable pageableRequest = PageRequest.of(page, size);

        Query query = new Query();
        if (StringUtils.isBlank(keyword)) {
            // find all
            Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, CompanyDocEnum.SORT_NAME.getKey()),
                    new Sort.Order(Sort.Direction.DESC, CompanyDocEnum.UPDATED_DATE.getKey()));
            query.with(pageableRequest).with(sort);
        } else if (keyword.trim().split(Constants.WHITE_SPACE).length == 1) {
            // Regex search
            Criteria regexCriteria = Criteria.where(CompanyDocEnum.NAME.getKey())
                    .regex(keyword, Constants.REGEX_IGNORE_CASE);
            Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, CompanyDocEnum.SORT_NAME.getKey()),
                    new Sort.Order(Sort.Direction.DESC, CompanyDocEnum.UPDATED_DATE.getKey()));
            query.addCriteria(regexCriteria).with(pageableRequest).with(sort);
        } else {
            // Full text search
            TextCriteria textCriteria = TextCriteria.forDefaultLanguage().caseSensitive(false).matchingAny(keyword);
            query = TextQuery.queryText(textCriteria).sortByScore().with(pageableRequest);
        }
        return query;
    }

    @Override
    public Page<CompanyDoc> searchFullTextByName(String keyword, int page, int size) {
        final Pageable pageableRequest = PageRequest.of(page, size);

        Query query = initSearchFullTextByName(keyword, page, size);
        List<CompanyDoc> companyDocs = mongoTemplate.find(query, CompanyDoc.class);
        long count = mongoTemplate.count(query, CompanyDoc.class);
        return new PageImpl<>(companyDocs, pageableRequest, count);
    }

    @Override
    public Page<CompanyDoc> searchFullTextByNameExceptStaff(String keyword, int page, int size, long userId) {
        final Pageable pageableRequest = PageRequest.of(page, size);

        Query query = initSearchFullTextByName(keyword, page, size);
        StringBuilder staffQuery = new StringBuilder(CompanyDocEnum.STAFFS.getKey());
        staffQuery.append(Constants.DOT).append(StaffShortEmbeddedEnum.USER_PROFILE.getKey())
                .append(Constants.DOT).append(UserProfileEmbeddedEnum.USER_PROFILE_ID.getKey());
        Criteria notStaffCriteria = Criteria.where(staffQuery.toString()).nin(userId);
        query.addCriteria(notStaffCriteria);
        List<CompanyDoc> companyDocs = mongoTemplate.find(query, CompanyDoc.class);
        long count = mongoTemplate.count(query, CompanyDoc.class);
        return new PageImpl<>(companyDocs, pageableRequest, count);
    }

    @Override
    public List<CompanyDoc> findNoneStaffCompany(int limit) {
        Criteria criteria = Criteria.where(CompanyDocEnum.STAFFS.getKey()).exists(false);
        Query query = new Query(criteria);
        query.limit(limit);
        return mongoTemplate.find(query, CompanyDoc.class);
    }

    @Override
    public void updateStaffsOfCompanies(List<CompanyDoc> companyDocs) {
        if (CollectionUtils.isNotEmpty(companyDocs)) {
            AtomicBoolean isExecuteBulk = new AtomicBoolean(false);
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, CompanyDoc.class);
            companyDocs.forEach(companyDoc -> {
                if (CollectionUtils.isNotEmpty(companyDoc.getStaffs())) {
                    Criteria criteria = Criteria.where(CompanyDocEnum.ID.getKey()).is(companyDoc.getId());
                    Query query = new Query(criteria);

                    companyDoc.getStaffs().forEach(staff -> {
                        Update update = new Update();
                        update.addToSet(CompanyDocEnum.STAFFS.getKey(), staff);
                        bulkOps.updateMulti(query, update);
                    });

                    isExecuteBulk.set(true);
                }
            });
            if (isExecuteBulk.get()) {
                bulkOps.execute();
            }
        }
    }

    @Override
    public void removeStaffInCompany(long companyId, long staffId) {
        Criteria criteria = Criteria.where(CompanyDocEnum.ID.getKey()).is(companyId);
        Query query = new Query(criteria);
        Update update = new Update();
        Criteria subCriteria = Criteria.where(StaffShortEmbeddedEnum.ID.getKey()).is(staffId);
        update.pull(CompanyDocEnum.STAFFS.getKey(), new Query(subCriteria).getQueryObject());
        mongoTemplate.updateFirst(query, update, CompanyDoc.class);
    }
}
