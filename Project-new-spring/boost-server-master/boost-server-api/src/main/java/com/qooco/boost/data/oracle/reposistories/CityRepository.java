package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.City;
import com.qooco.boost.data.oracle.entities.Province;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends Boot2JpaRepository<City, Long> {
    @Query("SELECT c FROM City c WHERE c.province = :province AND c.isDeleted = 0")
    Page<City> findAllByProvince(@Param(value = "province") Province province, Pageable pageable);

    @Query("SELECT COUNT (c.cityId) FROM City c WHERE c.province = :province AND c.isDeleted = 0")
    int countByProvince(@Param(value = "province") Province province);

    @Query("SELECT c FROM City c WHERE c.id = :id AND c.isDeleted = false ")
    City findValidById(@Param("id") Long id);
}