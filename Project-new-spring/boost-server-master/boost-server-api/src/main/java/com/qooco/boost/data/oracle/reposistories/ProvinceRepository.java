package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.Country;
import com.qooco.boost.data.oracle.entities.Province;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceRepository extends Boot2JpaRepository<Province, Long> {
    @Query("SELECT p FROM Province p WHERE p.country = :country AND p.isDeleted = 0")
    Page<Province> findAllByCountry(@Param("country") Country country, Pageable pageable);

    @Query("SELECT COUNT (p.provinceId) FROM Province p WHERE p.country = :country AND p.isDeleted = 0")
    int countByCountry(@Param(value = "country") Country country);

    Province findByProvinceId(Long provinceId);
}