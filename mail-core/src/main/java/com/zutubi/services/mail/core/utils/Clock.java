package com.zutubi.services.mail.core.utils;

import java.util.Date;

/**
 * An interface representing a clock.
 * <p/>
 * By abstracting the source of the time used in the system, we are
 * able to control the passage of time during testing.
 */
public interface Clock {

    /**
     * The current time in milliseconds from the epoc.
     *
     * @return the current time.
     */
    long currentTimeMillis();

    /**
     * Return a date instance representing the invocation time.
     *
     * @return the invocation time.
     */
    Date now();
}