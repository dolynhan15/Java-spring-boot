package com.example.demo.repository;

import com.example.demo.model.entity.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkRepository extends CrudRepository<Work, Integer> {
    @Query("SELECT w FROM Work w WHERE w.isDeleted = 0")
    Page<Work> getWorks(PageRequest pageable);

    @Query("SELECT w FROM Work w WHERE w.id = :id AND w.isDeleted = 0")
    Work findExistedById(@Param("id") Integer id);
}
