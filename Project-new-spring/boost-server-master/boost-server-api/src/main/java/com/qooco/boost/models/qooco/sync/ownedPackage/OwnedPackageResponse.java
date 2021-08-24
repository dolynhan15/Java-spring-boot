package com.qooco.boost.models.qooco.sync.ownedPackage;

import com.qooco.boost.models.qooco.sync.BaseQoocoSyncResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 9/24/2018 - 4:24 PM
*/
@Setter @Getter
public class OwnedPackageResponse extends BaseQoocoSyncResponse {

    private Map<String, Package> ownedPackages;
    private Date ownedPackagesTimestamp;

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
