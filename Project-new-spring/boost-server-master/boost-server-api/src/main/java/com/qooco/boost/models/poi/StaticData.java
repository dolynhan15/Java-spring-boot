package com.qooco.boost.models.poi;

import com.qooco.boost.utils.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class StaticData {
    private String module;
    private String key;
    private String locale;
    private String content;

    public boolean hasEmptyValue() {
        if (StringUtil.isEmpty(module) || StringUtil.isEmpty(key) || StringUtil.isEmpty(locale) || StringUtil.isEmpty(content)) {
            return true;
        }
        return false;
    }
}
