package com.qooco.boost.utils;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

public class RandomString {
    private static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String digits = "0123456789";
    private static final int RANDOM_LENGTH = 8;

    private final Random random;
    private final char[] symbols;
    private final char[] buf;

    public RandomString(int length, Random random, String symbols) {
        if (length < 1) throw new IllegalArgumentException();
        if (symbols.length() < 2) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    public RandomString(int length, Random random) {
        this(length, random, upper + digits);
    }

    public RandomString(int length) {
        this(length, new SecureRandom());
    }

    public RandomString() {
        this(RANDOM_LENGTH);
    }

    /**
     * Generate a random string.
     */
    public String generateString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }
}
