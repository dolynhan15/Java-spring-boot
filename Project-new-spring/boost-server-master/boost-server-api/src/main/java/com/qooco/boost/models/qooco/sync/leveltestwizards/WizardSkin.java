package com.qooco.boost.models.qooco.sync.leveltestwizards;

import java.util.Date;
import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/8/2018 - 11:59 AM
*/
public class WizardSkin {

    private long wizardId;
    private String textResId;
    private Date timestamp;

    private List<Question> questions;
    private List<TestByValue> testsByValue;

    public long getWizardId() {
        return wizardId;
    }

    public void setWizardId(long wizardId) {
        this.wizardId = wizardId;
    }

    public String getTextResId() {
        return textResId;
    }

    public void setTextResId(String textResId) {
        this.textResId = textResId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<TestByValue> getTestsByValue() {
        return testsByValue;
    }

    public void setTestsByValue(List<TestByValue> testsByValue) {
        this.testsByValue = testsByValue;
    }
}
