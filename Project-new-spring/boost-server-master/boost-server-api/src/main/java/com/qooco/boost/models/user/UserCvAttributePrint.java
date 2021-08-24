package com.qooco.boost.models.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class UserCvAttributePrint {

    private String name;
    private Map<String, Boolean> levels;

    public UserCvAttributePrint(String attributeName, int level) {
        this.name = attributeName;
        levels = new HashMap<>();
        for (int i = 1; i < 10; i++) {
            if (i <= level) {
                levels.put(String.valueOf(i), true);
            } else {
                levels.put(String.valueOf(i), false);
            }
        }
    }
}
