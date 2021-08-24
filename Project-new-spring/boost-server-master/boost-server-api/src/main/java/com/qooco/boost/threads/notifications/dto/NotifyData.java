package com.qooco.boost.threads.notifications.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter @Getter
public class NotifyData {
    private int type;
    private Object data;
    boolean isCounted;
    @JsonIgnore
    private Map<String, List<Long>> targets;

    public NotifyData(){
        targets = new HashMap<>();
    }
    public NotifyData(int type, boolean isCounted) {
        targets = new HashMap<>();
        this.type = type;
        this.isCounted = isCounted;
    }

    public void setTarget(String key, List<Long> values){
        targets.put(key, values);
    }

    public void setTarget(String key, Long value){
        targets.put(key, Lists.newArrayList(value));
    }


}
