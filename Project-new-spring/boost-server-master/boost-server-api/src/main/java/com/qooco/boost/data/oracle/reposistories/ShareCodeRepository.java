package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.ShareCode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ShareCodeRepository extends Boot2JpaRepository<ShareCode, Long> {
    @Query("SELECT u.userProfileId, c.province.provinceId, sc.referralCode.referralCodeId, sc.createdDate FROM ShareCode sc " +
            "JOIN UserProfile u ON u.userProfileId = sc.createdBy " +
            "JOIN City c ON c.cityId = u.city.cityId " +
            "WHERE sc.createdDate >= :startDate AND sc.createdDate <= :endDate AND sc.isDeleted = FALSE ")
    List<Object[]> findUserSharedCodeGroupByLocationAndDuration(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
