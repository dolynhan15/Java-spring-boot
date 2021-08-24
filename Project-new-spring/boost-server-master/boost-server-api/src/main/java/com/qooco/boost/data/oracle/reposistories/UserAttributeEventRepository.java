package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.ProfileAttribute;
import com.qooco.boost.data.oracle.entities.UserAttributeEvent;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAttributeEventRepository extends Boot2JpaRepository<UserAttributeEvent, Long> {
    default UserAttributeEvent findByUserAttributeAndEvent(Long userProfileId, ProfileAttribute attribute, int eventCode) {
        return findFirstByUserProfile_UserProfileIdAndAttributeAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(userProfileId, attribute, eventCode);
    }

    default UserAttributeEvent findByUserAttributeAndEvent(Long userProfileId, Long attributeId, int eventCode) {
        return findFirstByUserProfile_UserProfileIdAndAttribute_IdAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(userProfileId, attributeId, eventCode);
    }

    default UserAttributeEvent findByUserAndEvent(Long userProfileId, int eventCode) {
        return findFirstByUserProfile_UserProfileIdAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(userProfileId, eventCode);
    }

    UserAttributeEvent findFirstByUserProfile_UserProfileIdAndAttributeAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(Long userProfileId, ProfileAttribute attribute, int eventCode);
    UserAttributeEvent findFirstByUserProfile_UserProfileIdAndAttribute_IdAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(Long userProfileId, Long attributeId, int eventCode);
    UserAttributeEvent findFirstByUserProfile_UserProfileIdAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(Long userProfileId, int eventCode);
    UserAttributeEvent findByUserProfile_UserProfileIdAndAttribute_IdAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(Long userProfileId, Long attributeId, int eventCode);

    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "UPDATE USER_ATTRIBUTE_EVENT SET count = GREATEST(count + :adjustment, 0), UPDATED_DATE = sysdate "
            + "WHERE USER_PROFILE_ID = :userProfileId AND coalesce(PROFILE_ATTRIBUTE_ID, -9) = coalesce(:attributeId, -9) AND EVENT_CODE = :eventCode AND IS_DELETED = 0")
    int adjustCountByUserAttributeAndEvent(@Param("userProfileId") Long userProfileId,
                                           @Param("attributeId") Long attributeId,
                                           @Param("eventCode") int eventCode,
                                           @Param("adjustment") int adjustment);

    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "INSERT INTO USER_ATTRIBUTE_EVENT (ID, USER_PROFILE_ID, PROFILE_ATTRIBUTE_ID, EVENT_CODE, COUNT, IS_DELETED, CREATED_DATE, UPDATED_DATE, CREATED_BY, UPDATED_BY) " +
            " SELECT USER_ATTRIBUTE_EVENT_SEQ.NEXTVAL, :#{#uae.userProfile.userProfileId}, :#{#uae.attribute == null ? @repositoryUtils.nullValue : #uae.attribute.id}, :#{#uae.eventCode}, :#{#uae.count}, " +
            ":#{#uae.isDeleted}, :#{#uae.createdDate}, :#{#uae.updatedDate}, :#{#uae.createdBy?:@repositoryUtils.nullValue}, :#{#uae.updatedBy?:@repositoryUtils.nullValue} from DUAL" +
            " left join USER_ATTRIBUTE_EVENT on USER_PROFILE_ID = :#{#uae.userProfile.userProfileId} AND coalesce(PROFILE_ATTRIBUTE_ID, -9) = :#{#uae.attribute == null ? -9 : #uae.attribute.id} AND EVENT_CODE = :#{#uae.eventCode} AND IS_DELETED = 0 where id is null")
    /*@Query(nativeQuery = true, value = "MERGE INTO USER_ATTRIBUTE_EVENT USING DUAL ON USER_PROFILE_ID = :#{#uae.userProfile.userProfileId} AND PROFILE_ATTRIBUTE_ID = :#{#uae.attribute.id} AND EVENT_CODE = :#{#uae.eventCode} AND IS_DELETED = 0 " +
            "when not matched then insert (ID, USER_PROFILE_ID, PROFILE_ATTRIBUTE_ID, EVENT_CODE, COUNT, IS_DELETED, CREATED_DATE, UPDATED_DATE, CREATED_BY, UPDATED_BY) values ("
            + " USER_ATTRIBUTE_EVENT_SEQ.NEXTVAL, :#{#uae.userProfile.userProfileId}, :#{#uae.attribute.id}, :#{#uae.eventCode}, :#{#uae.count}, :#{#uae.isDeleted}, :#{#uae.createdDate}, :#{#uae.updatedDate}, :#{#uae.createdBy}, :#{#uae.updatedBy})")*/
    int insert(@Param("uae") UserAttributeEvent userAttributeEvent);

    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "UPDATE USER_ATTRIBUTE_EVENT e SET count = GREATEST(:#{#uae.count}, 0), UPDATED_DATE = :#{#uae.updatedDate} "
            + " WHERE ID = :#{#uae.id} AND IS_DELETED = 0 AND TRUNC(:#{#uae.updatedDate}) > TRUNC(e.UPDATED_DATE)")
    int updateCountBeforeDate(@Param("uae") UserAttributeEvent value);
}
