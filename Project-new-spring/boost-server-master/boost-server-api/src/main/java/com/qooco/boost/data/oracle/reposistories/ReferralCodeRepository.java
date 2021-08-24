package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.ReferralCode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ReferralCodeRepository extends Boot2JpaRepository<ReferralCode, Long> {

    @Query(value = "SELECT refCode FROM ReferralCode refCode WHERE refCode.owner.userProfileId =:userProfileId AND refCode.isExpired =:isExpired")
    ReferralCode findByOwnerAndExpiredStatus(@Param("userProfileId") Long userProfileId, @Param("isExpired") boolean isExpired);

    @Query(value = "SELECT rc FROM ReferralCode rc WHERE rc.owner.userProfileId =:userProfileId AND rc.code = :code AND rc.isExpired = false")
    ReferralCode findActiveByOwnerAndCode(@Param("userProfileId") Long userProfileId, @Param("code") String code);

    @Query(value = "SELECT rc FROM ReferralCode rc WHERE rc.owner.userProfileId NOT IN :userProfileId AND rc.code = :code AND rc.isExpired = false")
    ReferralCode findActiveByNotOwnerAndCode(@Param("userProfileId") Long userProfileId, @Param("code") String code);

    ReferralCode findByCode(String code);

    ReferralCode findFirstByOwner_UserProfileIdAndCreatedDateBefore(Long userId, Date createdDateEvent);

}
