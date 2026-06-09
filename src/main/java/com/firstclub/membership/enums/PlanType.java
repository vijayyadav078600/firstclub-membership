package com.firstclub.membership.enums;

public enum PlanType {
    MONTHLY("Monthly Plan", 30),
    QUARTERLY("Quarterly Plan", 90),
    YEARLY("Yearly Plan", 365);

    private final String displayName;
    private final int durationDays;

    PlanType(String displayName, int durationDays) {
        this.displayName = displayName;
        this.durationDays = durationDays;
    }

    public String getDisplayName() { return displayName; }
    public int getDurationDays() { return durationDays; }
}
