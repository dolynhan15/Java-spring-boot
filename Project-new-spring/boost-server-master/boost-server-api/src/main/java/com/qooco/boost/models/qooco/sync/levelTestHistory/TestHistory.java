package com.qooco.boost.models.qooco.sync.levelTestHistory;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter @Getter
public class TestHistory {
    private int level;
    private int minLevel;
    private int maxLevel;
    private double levelScore;
    private String scaleId;
    private Date timestamp;
    private String sessionId;
    private Long duration;
}
