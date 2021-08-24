package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.Benefit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BenefitRepository extends Boot2JpaRepository<Benefit, Integer> {

    @Query("SELECT b FROM Benefit b WHERE b.benefitId IN :ids")
    List<Benefit> findByIds(@Param("ids") long[] ids);

    @Query("SELECT COUNT(b.benefitId) FROM Benefit b WHERE b.benefitId IN :ids")
    int countByIds(@Param("ids") Long[] ids);
}
