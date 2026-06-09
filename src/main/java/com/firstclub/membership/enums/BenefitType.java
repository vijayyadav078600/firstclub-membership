package com.firstclub.membership.enums;

public enum BenefitType {
    FREE_DELIVERY("Free Delivery", "Free delivery on orders above minimum threshold"),
    DISCOUNT_PERCENTAGE("Extra Discount", "Extra percentage discount on selected items"),
    EARLY_ACCESS_SALES("Early Access", "Early access to sales and new arrivals"),
    PRIORITY_SUPPORT("Priority Support", "Dedicated priority customer support"),
    EXCLUSIVE_DEALS("Exclusive Deals", "Access to member-only exclusive deals"),
    EXCLUSIVE_COUPONS("Exclusive Coupons", "Monthly exclusive coupon codes"),
    FASTER_DELIVERY("Faster Delivery", "Expedited delivery option");

    private final String displayName;
    private final String description;

    BenefitType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
