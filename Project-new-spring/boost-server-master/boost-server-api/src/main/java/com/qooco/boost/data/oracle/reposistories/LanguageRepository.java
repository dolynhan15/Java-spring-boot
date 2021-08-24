package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: tnlong
 Date: 6/20/2018 - 11:14 AM
*/
@Repository
public interface LanguageRepository extends Boot2JpaRepository<Language, Long> {
    @Query("SELECT COUNT(l.languageId) FROM Language l WHERE l.languageId IN :ids AND l.isDeleted = false")
    int countByIds(@Param("ids") Long[] ids);

    @Query("SELECT l FROM Language l WHERE l.isDeleted = false AND l.languageId IN :ids ")
    List<Language> findByIds(@Param("ids") long[] ids);

    @Query("SELECT l FROM Language l WHERE  l.isDeleted = false ORDER BY l.name ASC")
    Page<Language> findAll(Pageable pageable);

    @Query("SELECT COUNT(l.languageId) FROM Language l WHERE l.isDeleted = false")
    long count();
}
