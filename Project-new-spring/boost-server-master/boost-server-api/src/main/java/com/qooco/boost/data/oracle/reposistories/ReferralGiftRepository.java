package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.ReferralGift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReferralGiftRepository extends Boot2JpaRepository<ReferralGift, Long> {
    @Query("SELECT rg.assessment.id FROM ReferralGift rg WHERE rg.assessment IS NOT NULL AND rg.isDeleted = false ORDER BY rg.assessment.id ASC ")
    List<Long> getAssessmentIds();

    @Query("SELECT rg FROM ReferralGift rg WHERE rg.id NOT IN :giftIds AND rg.remain <> 0 AND rg.isDeleted = false AND rg.expiredDate > SYS_EXTRACT_UTC(SYSTIMESTAMP) ORDER BY rg.coin ASC ")
    Page<ReferralGift> findAll(Pageable pageable, @Param("giftIds") Long[] giftIds);

    @Query("SELECT rg FROM ReferralGift rg WHERE rg.remain <> 0 AND rg.isDeleted = false AND rg.expiredDate > SYS_EXTRACT_UTC(SYSTIMESTAMP) ORDER BY rg.coin ASC ")
    Page<ReferralGift> findAll(Pageable pageable);

    @Query("SELECT COUNT(rg.id) FROM ReferralGift rg WHERE rg.id IN :ids AND rg.isDeleted = false ORDER BY rg.coin ASC ")
    int countByIds(@Param("ids") Long[] ids);

    @Query("SELECT rg FROM ReferralGift rg WHERE rg.id IN :ids AND rg.isDeleted = false ORDER BY rg.coin ASC ")
    List<ReferralGift> findByIds(@Param("ids") Long[] ids);

    @Query("SELECT rg FROM ReferralGift rg WHERE rg.assessment IS NOT NULL AND rg.assessment.id IN :ids AND rg.isDeleted = false ORDER BY rg.assessment.id ASC ")
    List<ReferralGift> findByAssessmentIds(@Param("ids") Long[] assessmentIds);
}