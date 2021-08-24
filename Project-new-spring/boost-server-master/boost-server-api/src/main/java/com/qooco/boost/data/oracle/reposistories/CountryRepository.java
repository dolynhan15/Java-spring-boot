package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends Boot2JpaRepository<Country, Long> {
    @Override
    @Query("SELECT c FROM Country c WHERE c.isDeleted = 0")
    Page<Country> findAll(Pageable pageable);

    @Query("SELECT c FROM Country c WHERE c.id = :id AND c.isDeleted = false ")
    Country findValidById(@Param("id") Long id);
}
