package com.qooco.boost.models.qooco.sync.ownedPackage;

import java.util.Date;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 9/24/2018 - 4:26 PM
*/
public class Package {

    private Date expires;
    private Date activationDate;
    private int limitPassCount;
    private int orderByLesson;
    private int orderByTopic;
    private String[] unlockedLessons;
    private int topicLimit;

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public Date getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Date activationDate) {
        this.activationDate = activationDate;
    }

    public int getLimitPassCount() {
        return limitPassCount;
    }

    public void setLimitPassCount(int limitPassCount) {
        this.limitPassCount = limitPassCount;
    }

    public int getOrderByLesson() {
        return orderByLesson;
    }

    public void setOrderByLesson(int orderByLesson) {
        this.orderByLesson = orderByLesson;
    }

    public int getOrderByTopic() {
        return orderByTopic;
    }

    public void setOrderByTopic(int orderByTopic) {
        this.orderByTopic = orderByTopic;
    }

    public String[] getUnlockedLessons() {
        return unlockedLessons;
    }

    public void setUnlockedLessons(String[] unlockedLessons) {
        this.unlockedLessons = unlockedLessons;
    }

    public int getTopicLimit() {
        return topicLimit;
    }

    public void setTopicLimit(int topicLimit) {
        this.topicLimit = topicLimit;
    }
}
