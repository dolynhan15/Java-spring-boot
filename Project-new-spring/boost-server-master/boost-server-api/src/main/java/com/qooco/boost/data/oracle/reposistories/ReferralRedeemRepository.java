package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.ReferralRedeem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 8/6/2018 - 5:22 PM
 */
@Repository
public interface ReferralRedeemRepository extends Boot2JpaRepository<ReferralRedeem, Long> {

    @Query("SELECT COUNT(r.referralRedeemId) FROM ReferralRedeem r WHERE r.codeId =:referralCodeId")
    int countByCodeId(@Param("referralCodeId") Long referralCodeId);

    List<ReferralRedeem> findByCodeId(Long referralCodeId);
}
