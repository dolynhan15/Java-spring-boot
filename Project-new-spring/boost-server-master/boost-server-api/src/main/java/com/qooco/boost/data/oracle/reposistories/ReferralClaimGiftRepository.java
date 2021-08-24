package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.ReferralClaimGift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ReferralClaimGiftRepository extends Boot2JpaRepository<ReferralClaimGift, Long> {

    @Query(value = "SELECT COUNT(rg.ID) FROM REFERRAL_CLAIM_GIFT rg WHERE rg.USER_PROFILE_ID = :userProfileId AND rg.REFERRAL_GIFT_ID IS NOT NULL ", nativeQuery = true)
    int countByOwner(@Param("userProfileId") Long userProfileId);

    @Query(value = "SELECT rg.REFERRAL_GIFT_ID FROM REFERRAL_CLAIM_GIFT rg WHERE rg.USER_PROFILE_ID = :userProfileId AND rg.REFERRAL_GIFT_ID IS NOT NULL ORDER BY rg.REFERRAL_GIFT_ID ASC ", nativeQuery = true)
    List<BigDecimal> findIdsByOwner(@Param("userProfileId") Long userProfileId);

    @Query("SELECT rg FROM ReferralClaimGift rg WHERE rg.owner = :userProfileId AND rg.gift IS NOT NULL ORDER BY GET_STATUS_OF_GIFT_BY_DURATION(rg.activeDate, rg.expiredDate, SYS_EXTRACT_UTC(SYSTIMESTAMP)) ASC, INTERVAL_OF_GIFT_BY_DURATION(rg.activeDate, rg.expiredDate, SYS_EXTRACT_UTC(SYSTIMESTAMP)) ASC,rg.id ASC ")
    Page<ReferralClaimGift> findByOwner(@Param("userProfileId") Long userProfileId, Pageable pageable);


    @Query(value = "SELECT COUNT(rg.ID) FROM REFERRAL_CLAIM_GIFT rg WHERE rg.USER_PROFILE_ID = :userProfileId AND rg.REFERRAL_GIFT_ID IS NOT NULL AND ACTIVE_DATE > SYS_EXTRACT_UTC(SYSTIMESTAMP) ", nativeQuery = true)
    int countInActiveByOwner(@Param("userProfileId") Long userProfileId);
}