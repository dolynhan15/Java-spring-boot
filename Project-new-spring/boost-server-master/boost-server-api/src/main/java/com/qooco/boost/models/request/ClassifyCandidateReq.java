package com.qooco.boost.models.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter @Getter @NoArgsConstructor
public class ClassifyCandidateReq {
    private long id;
    private long userCVId;
    private int action;

    public ClassifyCandidateReq(long id, long userProfileId, int action) {
        this.id = id;
        this.userCVId = userProfileId;
        this.action = action;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassifyCandidateReq that = (ClassifyCandidateReq) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
