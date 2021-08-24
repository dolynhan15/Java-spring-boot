package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.RoleCompany;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/21/2018 - 11:36 AM
 */
@Repository
public interface RoleCompanyRepository extends Boot2JpaRepository<RoleCompany, Long> {
    @Query("SELECT r FROM RoleCompany r WHERE r.name = :name AND r.isDeleted = false ")
    RoleCompany findByName(@Param("name") String name);

    @Query(value = "SELECT r FROM RoleCompany r WHERE r.name IN (:names) AND r.isDeleted = false ")
    List<RoleCompany> findByNames(@Param("names") List<String> names);
}
