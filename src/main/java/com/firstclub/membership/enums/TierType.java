package com.firstclub.membership.enums;

public enum TierType {
    SILVER("Silver", 1),
    GOLD("Gold", 2),
    PLATINUM("Platinum", 3);

    private final String displayName;
    private final int level;

    TierType(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() { return displayName; }
    public int getLevel() { return level; }

    public boolean isHigherThan(TierType other) {
        return this.level > other.level;
    }

    public boolean isLowerThan(TierType other) {
        return this.level < other.level;
    }
}
