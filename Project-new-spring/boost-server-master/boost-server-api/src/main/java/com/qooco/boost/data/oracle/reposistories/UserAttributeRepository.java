package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.ProfileAttribute;
import com.qooco.boost.data.oracle.entities.UserAttribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.qooco.boost.data.constants.AttributeConstant.*;

@Repository
public interface UserAttributeRepository extends Boot2JpaRepository<UserAttribute, Long> {
    default UserAttribute findByUserAttribute(Long userProfileId, ProfileAttribute attribute) {
        return findFirstByUserProfile_UserProfileIdAndAttributeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(userProfileId, attribute);
    }

    UserAttribute findFirstByUserProfile_UserProfileIdAndAttributeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(Long userProfileId, ProfileAttribute attribute);
    UserAttribute findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(Long userProfileId, Long attributeId);

    String CASE_SCORE_LEVEL = " CASE \n" +
            " WHEN score + :adjustment >= " + MIN_LEVEL_9 + " AND \"LEVEL\" < 9 THEN 9\n" +
            " WHEN score + :adjustment >= " + MIN_LEVEL_8 + " AND \"LEVEL\" < 8 THEN 8\n" +
            " WHEN score + :adjustment >= " + MIN_LEVEL_7 + " AND \"LEVEL\" < 7 THEN 7\n" +
            " WHEN score + :adjustment >= " + MIN_LEVEL_6 + " AND \"LEVEL\" < 6 THEN 6\n" +
            " WHEN score + :adjustment >= " + MIN_LEVEL_5 + " AND \"LEVEL\" < 5 THEN 5\n" +
            " WHEN score + :adjustment >= " + MIN_LEVEL_4 + " AND \"LEVEL\" < 4 THEN 4\n" +
            " WHEN score + :adjustment >= " + MIN_LEVEL_3 + " AND \"LEVEL\" < 3 THEN 3\n" +
            " WHEN score + :adjustment >= " + MIN_LEVEL_2 + " AND \"LEVEL\" < 2 THEN 2\n" +
            " WHEN score + :adjustment >= " + MIN_LEVEL_1 + " AND \"LEVEL\" < 1 THEN 1\n" +
            " ELSE \"LEVEL\" END ";

    String CASE_SCORE_NEW_LEVEL = " CASE \n" +
            " WHEN score + :adjustment >= " + MIN_LEVEL_9 + " AND \"LEVEL\" < 9 THEN 1\n" +
            " WHEN score + :adjustment >= " + MIN_LEVEL_8 + " AND \"LEVEL\" < 8 THEN 1\n" +
            " WHEN score + :adjustment >= " + MIN_LEVEL_7 + " AND \"LEVEL\" < 7 THEN 1\n" +
            " WHEN score + :adjustment >= " + MIN_LEVEL_6 + " AND \"LEVEL\" < 6 THEN 1\n" +
            " WHEN score + :adjustment >= " + MIN_LEVEL_5 + " AND \"LEVEL\" < 5 THEN 1\n" +
            " WHEN score + :adjustment >= " + MIN_LEVEL_4 + " AND \"LEVEL\" < 4 THEN 1\n" +
            " WHEN score + :adjustment >= " + MIN_LEVEL_3 + " AND \"LEVEL\" < 3 THEN 1\n" +
            " WHEN score + :adjustment >= " + MIN_LEVEL_2 + " AND \"LEVEL\" < 2 THEN 1\n" +
            " WHEN score + :adjustment >= " + MIN_LEVEL_1 + " AND \"LEVEL\" < 1 THEN 1\n" +
            " ELSE IS_NEW_LEVEL END ";

    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "UPDATE USER_ATTRIBUTE SET "
            + "IS_NEW_LEVEL = " + CASE_SCORE_NEW_LEVEL
            + ", \"LEVEL\" = " + CASE_SCORE_LEVEL
            + ", score = GREATEST(score + :adjustment, 0)"
            + " WHERE USER_PROFILE_ID = :userProfileId AND PROFILE_ATTRIBUTE_ID = :attributeId AND IS_DELETED = 0")
    int adjustScore(@Param("userProfileId") Long userProfileId,
                    @Param("attributeId") Long attributeId,
                    @Param("adjustment") int adjustment);

    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "INSERT INTO USER_ATTRIBUTE(ID, USER_PROFILE_ID, PROFILE_ATTRIBUTE_ID, SCORE, \"LEVEL\", IS_NEW_LEVEL, IS_DELETED, CREATED_DATE, UPDATED_DATE, CREATED_BY, UPDATED_BY) "
            + " SELECT USER_ATTRIBUTE_SEQ.NEXTVAL, :#{#ua.userProfile.userProfileId}, :#{#ua.attribute.id}, :#{#ua.score}, :#{#ua.level}, :#{#ua.newLevel}, :#{#ua.isDeleted}, :#{#ua.createdDate}, :#{#ua.updatedDate}, :#{#ua.createdBy?:@repositoryUtils.nullValue}, :#{#ua.updatedBy?:@repositoryUtils.nullValue} from DUAL"
            + " left join USER_ATTRIBUTE on USER_PROFILE_ID = :#{#ua.userProfile.userProfileId} AND PROFILE_ATTRIBUTE_ID = :#{#ua.attribute.id} AND IS_DELETED = 0 where id is null")
    /*@Query(nativeQuery = true, value = "merge into USER_ATTRIBUTE using DUAL on (USER_PROFILE_ID = :#{#ua.userProfile.userProfileId} AND PROFILE_ATTRIBUTE_ID = :#{#ua.attribute.id} AND IS_DELETED = 0)" +
            "    when not matched then insert (ID, USER_PROFILE_ID, PROFILE_ATTRIBUTE_ID, SCORE, \"LEVEL\", IS_NEW_LEVEL, IS_DELETED, CREATED_DATE, UPDATED_DATE, CREATED_BY, UPDATED_BY) values("
            + " USER_ATTRIBUTE_SEQ.NEXTVAL, :#{#ua.userProfile.userProfileId}, :#{#ua.attribute.id}, :#{#ua.score}, :#{#ua.level}, :#{#ua.newLevel}, :#{#ua.isDeleted}, :#{#ua.createdDate}, :#{#ua.updatedDate}, :#{#ua.createdBy}, :#{#ua.updatedBy} )")*/
    int insert(@Param("ua") UserAttribute userAttribute);

    default List<UserAttribute> findByNewLevel(Long userProfileId, boolean newLevel) {
        return findByUserProfile_UserProfileIdAndNewLevelAndIsDeletedFalse(userProfileId, newLevel);
    }

    List<UserAttribute> findByUserProfile_UserProfileIdAndNewLevelAndIsDeletedFalse(Long userProfileId, boolean newLevel);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE #{#entityName} SET newLevel = :newLevel WHERE id IN :ids AND newLevel <> :newLevel AND isDeleted = 0")
    int updateNewLevel(@Param("ids") Iterable<Long> ids, @Param("newLevel") boolean newLevel);


    default long countByUserProfile(Long userProfileId) {
        return countByUserProfile_UserProfileIdAndLevelGreaterThanAndIsDeletedFalseAndAttribute_IsDeletedFalse(userProfileId, 0);
    }

    long countByUserProfile_UserProfileIdAndLevelGreaterThanAndIsDeletedFalseAndAttribute_IsDeletedFalse(Long userProfileId, int level);

    @Query(value = "SELECT new com.qooco.boost.data.oracle.entities.UserAttribute(a, e.level, e.score) " +
            "FROM UserAttribute e RIGHT OUTER JOIN e.attribute a " +
            "ON(e.userProfile.userProfileId = :userProfileId) " +
            "ORDER BY COALESCE(e.level, 0) DESC, COALESCE(e.score, 0) DESC, a.name ASC",
            countQuery = "SELECT count(a.id) FROM ProfileAttribute a where 1 = 1 OR :userProfileId IS NULL"
    )
    Page<UserAttribute> findByUserProfile(@Param("userProfileId") Long userProfileId, Pageable pageable);

    @Query(value = "SELECT new com.qooco.boost.data.oracle.entities.UserAttribute(a, e.level, e.score) " +
            "FROM UserAttribute e RIGHT OUTER JOIN e.attribute a " +
            "ON(e.userProfile.userProfileId = :userProfileId) WHERE e.level > 0 " +
            "ORDER BY COALESCE(e.level, 0) DESC, COALESCE(e.score, 0) DESC, a.name ASC",
            countQuery = "SELECT count(a.id) FROM ProfileAttribute a where 1 = 1 OR :userProfileId IS NULL"
    )
    Page<UserAttribute> findActiveByUserProfile(@Param("userProfileId") Long userProfileId, Pageable pageable);
}
