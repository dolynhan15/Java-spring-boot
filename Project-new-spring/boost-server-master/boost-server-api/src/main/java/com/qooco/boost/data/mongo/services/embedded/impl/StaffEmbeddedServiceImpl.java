package com.qooco.boost.data.mongo.services.embedded.impl;

import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.enumeration.doc.CompanyDocEnum;
import com.qooco.boost.data.enumeration.embedded.StaffShortEmbeddedEnum;
import com.qooco.boost.data.enumeration.embedded.UserProfileEmbeddedEnum;
import com.qooco.boost.data.mongo.embedded.StaffShortEmbedded;
import com.qooco.boost.data.mongo.entities.CompanyDoc;
import com.qooco.boost.data.mongo.services.embedded.StaffEmbeddedService;
import com.qooco.boost.utils.MongoInitData;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class StaffEmbeddedServiceImpl implements StaffEmbeddedService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void update(StaffShortEmbedded staff) {
        updateInCompanyDoc(staff);
    }

    private void updateInCompanyDoc(StaffShortEmbedded staff) {
        if (Objects.nonNull(staff)) {
            String staffPrefix = StringUtil.append(CompanyDocEnum.STAFFS.getKey(), Constants.DOLLAR);
            Criteria criteria = new Criteria().andOperator(
                    Criteria.where(StringUtil.append(UserProfileEmbeddedEnum.USER_TYPE.getKey())).is(UserType.SELECT),
                    Criteria.where(StringUtil.append(StaffShortEmbeddedEnum.ID.getKey())).is(staff.getId())
            );
            Criteria elementMatching = Criteria.where(CompanyDocEnum.STAFFS.getKey()).elemMatch(criteria);

            multiUpdateEmbedded(elementMatching, staffPrefix, staff, CompanyDoc.class);
        }
    }

    private void multiUpdateEmbedded(Criteria criteria, String prefix, StaffShortEmbedded staff, Class<?> clazz) {
        if (Objects.nonNull(staff)) {
            Map<String, Object> map = MongoInitData.initStaffShortEmbedded(prefix, staff);
            if (MapUtils.isNotEmpty(map)) {
                Query query = new Query(criteria);
                Update update = new Update();
                map.forEach(update::set);
                mongoTemplate.updateMulti(query, update, clazz);
            }
        }
    }
}
