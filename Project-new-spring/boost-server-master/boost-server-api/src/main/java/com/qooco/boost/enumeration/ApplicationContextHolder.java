package com.qooco.boost.enumeration;

import org.springframework.context.ApplicationContext;

public enum ApplicationContextHolder {
    INSTANCE;
    private ApplicationContext ctx;

    public void setApplicationContext(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public ApplicationContext getCtx() {
        return this.ctx;
    }
}
