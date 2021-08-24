package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.ProfileAttribute;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import static javax.persistence.LockModeType.PESSIMISTIC_READ;

@Repository
public interface ProfileAttributeRepository extends Boot2JpaRepository<ProfileAttribute, Long> {

    long countByIsDeletedFalse();

    @Lock(PESSIMISTIC_READ)
    ProfileAttribute findById__(Long id);

    default ProfileAttribute findByIdWithLock(Long id) {
        return findById__(id);
    }
}
