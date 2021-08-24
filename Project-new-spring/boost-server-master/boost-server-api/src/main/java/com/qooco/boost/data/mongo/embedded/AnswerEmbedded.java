package com.qooco.boost.data.mongo.embedded;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/8/2018 - 11:38 AM
*/
public class AnswerEmbedded {

    private long id;

    private String image;
    private long testId;
    private String textResId;
    private String descResId;
    private boolean showInDebugModeOnly;

    private String text;
    private String value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTestId() {
        return testId;
    }

    public void setTestId(long testId) {
        this.testId = testId;
    }

    public String getTextResId() {
        return textResId;
    }

    public void setTextResId(String textResId) {
        this.textResId = textResId;
    }

    public String getDescResId() {
        return descResId;
    }

    public void setDescResId(String descResId) {
        this.descResId = descResId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isShowInDebugModeOnly() {
        return showInDebugModeOnly;
    }

    public void setShowInDebugModeOnly(boolean showInDebugModeOnly) {
        this.showInDebugModeOnly = showInDebugModeOnly;
    }
}
