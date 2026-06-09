package com.firstclub.membership.enums;

public enum CohortType {
    REGULAR("Regular", 1),
    PREMIUM("Premium", 2),
    VIP("VIP", 3),
    ENTERPRISE("Enterprise", 4);

    private final String displayName;
    private final int level;

    CohortType(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() { return displayName; }
    public int getLevel() { return level; }
}
