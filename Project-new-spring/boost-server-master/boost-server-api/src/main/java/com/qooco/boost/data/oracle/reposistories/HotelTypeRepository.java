package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.HotelType;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelTypeRepository  extends Boot2JpaRepository<HotelType, Long> {
}