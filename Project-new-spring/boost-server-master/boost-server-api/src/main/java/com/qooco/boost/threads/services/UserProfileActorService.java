package com.qooco.boost.threads.services;


import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.entities.UserProfile;

import java.util.List;

public interface UserProfileActorService {
    UserProfile updateLazyValue(UserProfile userProfile);

    List<UserProfile> updateLazyValue(List<UserProfile> userProfile);

    UserFit updateLazyValue(UserFit userFit);

    List<UserFit> updateLazyValueUserFit(List<UserFit> userFits);
}
