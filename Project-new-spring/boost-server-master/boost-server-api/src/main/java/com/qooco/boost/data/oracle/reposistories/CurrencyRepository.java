package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.Currency;
import org.springframework.stereotype.Repository;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/4/2018 - 8:22 AM
*/
@Repository
public interface CurrencyRepository extends Boot2JpaRepository<Currency, Long> {

    Currency findByCode(String code);

}
