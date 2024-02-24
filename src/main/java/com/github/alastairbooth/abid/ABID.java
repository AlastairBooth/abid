package com.github.alastairbooth.abid;


import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class ABID {

    private static final char[] CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final AtomicInteger counter = new AtomicInteger();
    private static long instant;

    private final String id;

    @SuppressWarnings("unused")
    public ABID() throws ABIDException {
        this.id = generate();
    }

    private ABID(String string) {
        this.id = string;
    }

    @Override
    public String toString() {
        return id;
    }

    @SuppressWarnings("unused")
    public static ABID fromString(String string) {
        return new ABID(string);
    }

    private static String generate() throws ABIDException {
        long now = getTimeElement();
        if (instant != now) {
            instant = now;
            counter.set(0);
        }
        int c = counter.getAndIncrement();
        if (c > (Math.pow(CHARS.length, 2) - 1)) {
            throw new ABIDException("Too many IDs generated at the same time. New Ids could not be created as they will not be unique. Try creating fewer Ids within the same second");
        }
        String cString = StringUtils.rightPad(String.valueOf(now), 10, CHARS[0]);
        cString = StringUtils.rightPad(cString + c, 12, CHARS[0]);
        return encode(Long.parseLong(cString));
    }

    private static long getTimeElement() {
        long now = Instant.now().toEpochMilli();
        return (int) Math.floor(now/1000d);
    }

    private static String encode(long victim) {
        final List<Character> list = new ArrayList<>();
        final int length = CHARS.length;

        do {
            list.add(CHARS[(int) (victim % length)]);
            victim /= length;
        } while (victim > 0);

        StringBuilder stringBuilder = new StringBuilder();
        list.forEach(stringBuilder::append);
        return stringBuilder.toString();
    }
}