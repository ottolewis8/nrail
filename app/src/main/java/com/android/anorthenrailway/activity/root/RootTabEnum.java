package com.android.anorthenrailway.activity.root;

public enum RootTabEnum {
    TAB_HOME(0),
    TAB_PLAN_BUY(1),
    TAB_LIVE(2),
    TAB_MY_TICKETS(3),
    TAB_MORE(4);

    private int value;

    RootTabEnum(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }

    public static int count() { return 5; }

    public static RootTabEnum valueOf(int i) {
        switch (i) {
            case 0: return TAB_HOME;
            case 1: return TAB_PLAN_BUY;
            case 2: return TAB_LIVE;
            case 3: return TAB_MY_TICKETS;
            case 4: return TAB_MORE;
            default: return null;
        }
    }
}
