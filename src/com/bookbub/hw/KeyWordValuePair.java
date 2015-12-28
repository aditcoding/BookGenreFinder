package com.bookbub.hw;

/**
 * Created by adi on 12/9/15.
 */
public class KeyWordValuePair {
    private final String keyword;
    private final double value;

    public KeyWordValuePair(String keyword, double value) {
        this.keyword = keyword;
        this.value = value;
    }

    public String getKeyword() {
        return keyword;
    }

    public double getValue() {
        return value;
    }
}
