package com.qooco.boost.data.mongo.embedded;

import java.util.Date;
import java.util.Objects;

public class RejectedUserCvEmbedded {
    private Long userCvId;
    private Date rejectedDate;

    public RejectedUserCvEmbedded() {
    }

    public RejectedUserCvEmbedded(Long userCvId) {
        this.userCvId = userCvId;
    }

    public RejectedUserCvEmbedded(Long userCvId, Date rejectedDate) {
        this.userCvId = userCvId;
        this.rejectedDate = rejectedDate;
    }

    public Long getUserCvId() {
        return userCvId;
    }

    public void setUserCvId(Long userCvId) {
        this.userCvId = userCvId;
    }

    public Date getRejectedDate() {
        return rejectedDate;
    }

    public void setRejectedDate(Date rejectedDate) {
        this.rejectedDate = rejectedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RejectedUserCvEmbedded that = (RejectedUserCvEmbedded) o;
        return Objects.equals(userCvId, that.userCvId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userCvId);
    }
}
