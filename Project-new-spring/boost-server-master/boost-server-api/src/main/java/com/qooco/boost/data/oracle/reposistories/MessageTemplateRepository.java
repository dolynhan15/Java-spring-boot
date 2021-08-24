package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.MessageTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MessageTemplateRepository extends Boot2JpaRepository<MessageTemplate, Long> {

    @Query("SELECT mt FROM MessageTemplate mt WHERE mt.isDeleted = 0")
    Page<MessageTemplate> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM MESSAGE_TEMPLATE mt WHERE mt.IS_DELETED = 0 AND mt.UPDATED_DATE > :lastTime AND (:limit = 0 OR ROWNUM <= :limit) ORDER BY mt.ID", nativeQuery = true)
    List<MessageTemplate> findAll(@Param("lastTime") Date lastTime, @Param("limit")  int limit);

}
