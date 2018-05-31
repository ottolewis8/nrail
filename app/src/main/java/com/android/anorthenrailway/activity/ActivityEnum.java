package com.android.anorthenrailway.activity;

public enum ActivityEnum {
    ROOT(0),
    TICKETS(1),
    BARCODE(2);

    private int value;

    ActivityEnum(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }

    public static int count() { return 5; }

    public static ActivityEnum valueOf(int i) {
        switch (i) {
            case 0: return ROOT;
            case 1: return TICKETS;
            case 2: return BARCODE;
            default: return null;
        }
    }
}