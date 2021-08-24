package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.Assessment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface AssessmentRepository extends Boot2JpaRepository<Assessment, Long> {
    @Query("SELECT COUNT(a.id) FROM Assessment a WHERE a.id IN :ids  AND a.isDeleted = false ORDER BY a.name ASC ")
    int countByIds(@Param("ids") Long[] ids);

    @Query("SELECT max(a.updatedDate) FROM Assessment a")
    Date getLatestUpdate();

    @Query("SELECT a FROM Assessment a WHERE a.type =:type AND a.isDeleted = false ORDER BY a.name ASC ")
    Page<Assessment> findAll(Pageable pageable, @Param("type") Integer type);

    @Query("SELECT a FROM Assessment a WHERE a.isDeleted = false ORDER BY a.name ASC ")
    Page<Assessment> findAll(Pageable pageable);

    @Query("SELECT a FROM Assessment a WHERE a.assessmentLevels IS EMPTY AND a.isDeleted = false ORDER BY a.name ASC ")
    List<Assessment> getNoneLevelAssessment();

    @Query("SELECT COUNT(a.id) FROM Assessment a WHERE a.type = :type AND a.isDeleted = false")
    int countByType(@Param("type") Integer type);

    @Query("SELECT a FROM Assessment a WHERE a.id IN :ids  AND a.isDeleted = false ORDER BY a.name ASC ")
    List<Assessment> findByIds(@Param("ids") List<Long> ids);

    @Query("SELECT a FROM Assessment a WHERE a.id NOT IN :ids AND a.isDeleted = false ORDER BY a.id ASC")
    List<Assessment> findAssessmentsExceptIds(@Param("ids") List<Long> assessmentIds);

    @Query("SELECT a FROM Assessment a WHERE a.isDeleted = false ORDER BY a.id ASC")
    List<Assessment> findAssessments();

    @Modifying
    @Transactional
    @Query("UPDATE Assessment a SET a.isDeleted = :isDelete WHERE a.id = :id")
    void setIsDeleteById(@Param("id") Long id, @Param("isDelete") boolean isDelete);

    @Query("SELECT a FROM Assessment a WHERE a.id = :id AND a.isDeleted = false ")
    Assessment findValidById(@Param("id") Long id);
}
