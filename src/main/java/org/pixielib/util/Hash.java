package org.pixielib.util;

public class Hash {

    public static long hash(byte[] bytes) {

        long h;
        int i;

        for (h = 0, i = 0; i < bytes.length; ++i) {
            h ^= bytes[i];
            h *= 1099511628211L;
        }

        return h;
    }
}
