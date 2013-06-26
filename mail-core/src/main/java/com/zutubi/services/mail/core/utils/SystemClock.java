package com.zutubi.services.mail.core.utils;

import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * A implementation of the {@link Clock} interface backed by the {@link System#currentTimeMillis()}
 */
@Component
public class SystemClock implements Clock {

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public Date now() {
        return new Date();
    }
}
