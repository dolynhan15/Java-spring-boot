package com.qooco.boost.data.mongo.embedded;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/8/2018 - 11:50 AM
*/
public class QuestionEmbedded {

    private long id;

    private String type;
    private String textResId;

    private List<AnswerEmbedded> answers;

    private String imageResId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTextResId() {
        return textResId;
    }

    public void setTextResId(String textResId) {
        this.textResId = textResId;
    }

    public List<AnswerEmbedded> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerEmbedded> answers) {
        this.answers = answers;
    }

    public String getImageResId() {
        return imageResId;
    }

    public void setImageResId(String imageResId) {
        this.imageResId = imageResId;
    }
}
