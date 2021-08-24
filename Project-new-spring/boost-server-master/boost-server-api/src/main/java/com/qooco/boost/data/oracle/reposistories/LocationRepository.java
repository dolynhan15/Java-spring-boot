package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.model.count.CountByCompany;
import com.qooco.boost.data.oracle.entities.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface LocationRepository extends Boot2JpaRepository<Location, Long> {
    @Query("SELECT l FROM Location l WHERE l.company.companyId = :companyId AND l.company.isDeleted = false " +
            "AND l.city.cityId = :cityId AND l.city.isDeleted = false " +
            "AND l.address IS NULL AND l.isDeleted = false ")
    Location findByCompanyIdAndCityIdAndNullAddress(@Param("companyId") Long companyId, @Param("cityId") Long cityId);

    @Query("SELECT l FROM Location l WHERE l.company.companyId = :companyId AND l.company.isDeleted = false " +
            "AND l.city.cityId = :cityId AND l.city.isDeleted = false " +
            "AND LOWER(l.address) = :address AND l.isDeleted = false ")
    Location findByCompanyIdAndCityIdAndAddress(@Param("companyId") Long companyId, @Param("cityId") Long cityId, @Param("address") String address);

    @Query("SELECT l FROM Location l WHERE l.company.companyId = :companyId AND l.company.isDeleted = false " +
            "AND l.city.cityId IN (:cityIds) AND l.city.isDeleted = false AND l.isDeleted = false ")
    List<Location> findByCompanyIdAndCityId(@Param("companyId") Long companyId, @Param("cityIds") List<Long> cityIds);

    @Query("SELECT l FROM Location l WHERE l.company.companyId = :companyId AND l.isDeleted = false ORDER BY  l.city.province.country.countryName ASC, l.city.province.name ASC, l.city.cityName ASC, REGEXP_REPLACE(l.address, '^[0-9]+', '') ASC, REGEXP_SUBSTR(l.address, '^[0-9]+') ASC ")
    List<Location> findAllByCompanyCompanyId(@Param("companyId") Long companyId);

    @Query("select NEW com.qooco.boost.data.model.count.CountByCompany(l.company.companyId, count(l.company.companyId)) from Location l where l.company.companyId in :companyIds AND l.isDeleted = false group by l.company.companyId")
    List<CountByCompany> findAllByCompanyCompanyIds(@Param("companyIds") List<Long> companyIds);

    @Query("SELECT l FROM Location l WHERE l.company.companyId = :companyId AND l.locationId IN :ids AND l.isDeleted = false ")
    List<Location> findByIdsAndCompanyId(@Param("companyId") Long companyIds, @Param("ids") List<Long> ids);

    @Query("SELECT l FROM Location l WHERE l.company.companyId = :companyId AND l.isDeleted = false ORDER BY  l.city.province.country.countryName ASC, l.city.province.name ASC, l.city.cityName ASC, REGEXP_REPLACE(l.address, '^[0-9]+', '') ASC, REGEXP_SUBSTR(l.address, '^[0-9]+') ASC ")
    Page<Location> findByCompany(@Param("companyId") long companyId, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE Location l SET l.isPrimary = false WHERE l.company.companyId = :companyId")
    void updateNonePrimaryForCompany(@Param("companyId") long companyId);

    @Query("SELECT COUNT(l.locationId) FROM Location l WHERE l.company.companyId = :companyId AND l.isDeleted = false ")
    int countByCompany(@Param("companyId") Long companyId);
}