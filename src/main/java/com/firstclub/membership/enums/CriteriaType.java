package com.firstclub.membership.enums;

public enum CriteriaType {
    ORDER_COUNT("Order Count", "Minimum number of completed orders"),
    ORDER_VALUE_MONTHLY("Monthly Order Value", "Minimum total order value in current month"),
    ORDER_VALUE_TOTAL("Total Order Value", "Minimum cumulative order value since joining"),
    COHORT_MEMBERSHIP("Cohort Membership", "User belongs to a specific cohort");

    private final String displayName;
    private final String description;

    CriteriaType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
