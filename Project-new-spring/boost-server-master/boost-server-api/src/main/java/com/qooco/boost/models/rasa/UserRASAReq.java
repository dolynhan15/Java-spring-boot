package com.qooco.boost.models.rasa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;

import java.util.Locale;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserRASAReq {
    private String user;
    @Getter
    private Locale locale;
    private static final int USER_ID_INDEX = 0;
    private static final int USER_TYPE_INDEX = 1;
    public long getUserId() {
        return StringUtils.isNotBlank(user) ? Long.parseLong(user.trim().split("\\.")[USER_ID_INDEX]) : -1L;
    }

    public int getUserType() {
        return Integer.parseInt(user.trim().split("\\.")[USER_TYPE_INDEX]);
    }
}
