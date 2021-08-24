package com.qooco.boost.localization;

import com.google.gson.Gson;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.context.support.AbstractMessageSource;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public abstract class DBMessageSource extends AbstractMessageSource {


    protected Logger logger = LogManager.getLogger(DBMessageSource.class);

    protected Messages messages;

    public void initMessageIfNullable() {
        if (Objects.isNull(messages)) {
            messages = new Messages();
        }
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        String msg = messages.getMessage(code, locale);
        return createMessageFormat(msg, locale);
    }

    public String getMessage(String code, String locale) {
        return messages.getMessage(code, getLocale(locale));
    }

    private Locale getLocale(String locale) {
        if (StringUtils.isNotBlank(locale)) {
            return new Locale(locale);
        }
        return Locale.US;
    }

    /**
     * Messages bundle
     */
    protected static final class Messages {

        /* <code, <locale, message>> */
        private Map<String, Map<Locale, String>> messages;

        public void addMessage(String code, Locale locale, String msg) {
            if (messages == null)
                messages = new HashMap<String, Map<Locale, String>>();

            Map<Locale, String> data = messages.computeIfAbsent(code, k -> new HashMap<Locale, String>());

            data.put(locale, msg);
        }

        public String getMessage(String code, Locale locale) {
            if (Objects.nonNull(messages)) {
                Map<Locale, String> data = messages.get(code);
                return data != null ? data.get(locale) : null;
            }
            return null;
        }

        @Override
        public String toString() {
            return new Gson().toJson(messages);
        }
    }
}
