package org.pixielib.util;

import java.security.SecureRandom;

public class RandomPerm {

    private int[] table;
    private SecureRandom sr;

    public long get(int i) {
        if (i >= table.length)
            throw new IndexOutOfBoundsException("permutation index out of range.");

        return table[i];
    }

    public void generate(int n) {
        table = new int[n];

        sr = new SecureRandom();
        for (int i = 0, j; i < n; ++i) {
            j = uniform(i);
            table[i] = table[j];
            table[j] = i;
        }
    }

    private int uniform(int i) {
        if (i == 0)
            return 0;

        return sr.nextInt(i);
    }
}
