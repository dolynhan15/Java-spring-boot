package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.UserWallet;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWalletRepository extends Boot2JpaRepository<UserWallet, Long> {
}