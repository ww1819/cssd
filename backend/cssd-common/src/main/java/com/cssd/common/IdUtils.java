package com.cssd.common;

import com.github.f4b6a3.uuid.UuidCreator;

public final class IdUtils {
    private IdUtils() {
    }

    public static String uuid7() {
        return UuidCreator.getTimeOrderedEpoch().toString();
    }
}
