package com.firstclub.membership.config;

import com.firstclub.membership.entity.*;
import com.firstclub.membership.enums.*;
import com.firstclub.membership.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Seeds the in-memory H2 database with plans, tiers, benefits, criteria, and demo users.
 * All tier configuration (benefits, thresholds) lives here — change the numbers
 * without touching any business logic.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final MembershipPlanRepository planRepository;
    private final MembershipTierRepository tierRepository;
    private final TierBenefitRepository benefitRepository;
    private final TierCriteriaRepository criteriaRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) {
        seedPlans();
        seedTiers();
        seedUsers();
        log.info("=== FirstClub Membership data seeding complete ===");
        log.info("Swagger UI  → http://localhost:8080/swagger-ui.html");
        log.info("H2 Console  → http://localhost:8080/h2-console  (JDBC URL: jdbc:h2:mem:firstclubdb)");
    }

    private void seedPlans() {
        planRepository.save(MembershipPlan.builder()
                .planType(PlanType.MONTHLY)
                .price(new BigDecimal("299.00"))
                .durationDays(30)
                .description("Monthly membership with full tier benefits. Best for trying out.")
                .build());

        planRepository.save(MembershipPlan.builder()
                .planType(PlanType.QUARTERLY)
                .price(new BigDecimal("749.00"))
                .durationDays(90)
                .description("Quarterly membership — save 17% vs monthly. Great value.")
                .build());

        planRepository.save(MembershipPlan.builder()
                .planType(PlanType.YEARLY)
                .price(new BigDecimal("2399.00"))
                .durationDays(365)
                .description("Annual membership — save 33% vs monthly. Best value.")
                .build());

        log.info("Seeded 3 membership plans");
    }

    private void seedTiers() {
        MembershipTier silver = seedSilverTier();
        MembershipTier gold = seedGoldTier();
        MembershipTier platinum = seedPlatinumTier();
        log.info("Seeded 3 tiers: Silver (level 1), Gold (level 2), Platinum (level 3)");
    }

    private MembershipTier seedSilverTier() {
        MembershipTier silver = tierRepository.save(MembershipTier.builder()
                .tierType(TierType.SILVER)
                .tierLevel(1)
                .description("Entry-level membership tier with core benefits.")
                .build());

        benefitRepository.save(TierBenefit.builder()
                .tier(silver)
                .benefitType(BenefitType.FREE_DELIVERY)
                .value(new BigDecimal("500.00"))
                .description("Free delivery on orders above ₹500")
                .build());

        benefitRepository.save(TierBenefit.builder()
                .tier(silver)
                .benefitType(BenefitType.DISCOUNT_PERCENTAGE)
                .value(new BigDecimal("5"))
                .description("5% extra discount on all orders")
                .build());

        benefitRepository.save(TierBenefit.builder()
                .tier(silver)
                .benefitType(BenefitType.EARLY_ACCESS_SALES)
                .value(BigDecimal.ZERO)
                .description("24-hour early access to sales")
                .build());

        // Criteria: 3+ orders OR ₹1000 monthly spend OR REGULAR cohort (auto-qualify)
        criteriaRepository.save(TierCriteria.builder()
                .tier(silver)
                .criteriaType(CriteriaType.ORDER_COUNT)
                .thresholdValue(new BigDecimal("3"))
                .build());

        criteriaRepository.save(TierCriteria.builder()
                .tier(silver)
                .criteriaType(CriteriaType.ORDER_VALUE_MONTHLY)
                .thresholdValue(new BigDecimal("1000.00"))
                .build());

        criteriaRepository.save(TierCriteria.builder()
                .tier(silver)
                .criteriaType(CriteriaType.COHORT_MEMBERSHIP)
                .thresholdValue(BigDecimal.ONE)
                .requiredCohort(CohortType.REGULAR)
                .build());

        return silver;
    }

    private MembershipTier seedGoldTier() {
        MembershipTier gold = tierRepository.save(MembershipTier.builder()
                .tierType(TierType.GOLD)
                .tierLevel(2)
                .description("Mid-tier with enhanced discounts and exclusive deals.")
                .build());

        benefitRepository.save(TierBenefit.builder()
                .tier(gold)
                .benefitType(BenefitType.FREE_DELIVERY)
                .value(new BigDecimal("200.00"))
                .description("Free delivery on orders above ₹200")
                .build());

        benefitRepository.save(TierBenefit.builder()
                .tier(gold)
                .benefitType(BenefitType.DISCOUNT_PERCENTAGE)
                .value(new BigDecimal("10"))
                .description("10% extra discount on all orders")
                .build());

        benefitRepository.save(TierBenefit.builder()
                .tier(gold)
                .benefitType(BenefitType.EARLY_ACCESS_SALES)
                .value(BigDecimal.ZERO)
                .description("48-hour early access to sales")
                .build());

        benefitRepository.save(TierBenefit.builder()
                .tier(gold)
                .benefitType(BenefitType.EXCLUSIVE_DEALS)
                .value(BigDecimal.ZERO)
                .description("Access to Gold member exclusive deals")
                .build());

        benefitRepository.save(TierBenefit.builder()
                .tier(gold)
                .benefitType(BenefitType.EXCLUSIVE_COUPONS)
                .value(BigDecimal.ZERO)
                .description("2 exclusive coupon codes per month")
                .metadata("{\"coupons_per_month\": 2}")
                .build());

        // Criteria: 10+ orders OR ₹5000 monthly spend OR PREMIUM cohort
        criteriaRepository.save(TierCriteria.builder()
                .tier(gold)
                .criteriaType(CriteriaType.ORDER_COUNT)
                .thresholdValue(new BigDecimal("10"))
                .build());

        criteriaRepository.save(TierCriteria.builder()
                .tier(gold)
                .criteriaType(CriteriaType.ORDER_VALUE_MONTHLY)
                .thresholdValue(new BigDecimal("5000.00"))
                .build());

        criteriaRepository.save(TierCriteria.builder()
                .tier(gold)
                .criteriaType(CriteriaType.COHORT_MEMBERSHIP)
                .thresholdValue(new BigDecimal("2"))
                .requiredCohort(CohortType.PREMIUM)
                .build());

        return gold;
    }

    private MembershipTier seedPlatinumTier() {
        MembershipTier platinum = tierRepository.save(MembershipTier.builder()
                .tierType(TierType.PLATINUM)
                .tierLevel(3)
                .description("Premium tier with maximum benefits and priority support.")
                .build());

        benefitRepository.save(TierBenefit.builder()
                .tier(platinum)
                .benefitType(BenefitType.FREE_DELIVERY)
                .value(BigDecimal.ZERO)
                .description("Free delivery on ALL orders — no minimum")
                .build());

        benefitRepository.save(TierBenefit.builder()
                .tier(platinum)
                .benefitType(BenefitType.DISCOUNT_PERCENTAGE)
                .value(new BigDecimal("15"))
                .description("15% extra discount on all orders")
                .build());

        benefitRepository.save(TierBenefit.builder()
                .tier(platinum)
                .benefitType(BenefitType.EARLY_ACCESS_SALES)
                .value(BigDecimal.ZERO)
                .description("72-hour early access to sales and new launches")
                .build());

        benefitRepository.save(TierBenefit.builder()
                .tier(platinum)
                .benefitType(BenefitType.EXCLUSIVE_DEALS)
                .value(BigDecimal.ZERO)
                .description("Access to Platinum-only flash sales")
                .build());

        benefitRepository.save(TierBenefit.builder()
                .tier(platinum)
                .benefitType(BenefitType.EXCLUSIVE_COUPONS)
                .value(BigDecimal.ZERO)
                .description("5 exclusive coupon codes per month")
                .metadata("{\"coupons_per_month\": 5}")
                .build());

        benefitRepository.save(TierBenefit.builder()
                .tier(platinum)
                .benefitType(BenefitType.PRIORITY_SUPPORT)
                .value(BigDecimal.ZERO)
                .description("Dedicated priority support with < 2h response time")
                .build());

        benefitRepository.save(TierBenefit.builder()
                .tier(platinum)
                .benefitType(BenefitType.FASTER_DELIVERY)
                .value(BigDecimal.ZERO)
                .description("Next-day delivery available on eligible orders")
                .build());

        // Criteria: 25+ orders OR ₹15000 monthly spend OR VIP cohort
        criteriaRepository.save(TierCriteria.builder()
                .tier(platinum)
                .criteriaType(CriteriaType.ORDER_COUNT)
                .thresholdValue(new BigDecimal("25"))
                .build());

        criteriaRepository.save(TierCriteria.builder()
                .tier(platinum)
                .criteriaType(CriteriaType.ORDER_VALUE_MONTHLY)
                .thresholdValue(new BigDecimal("15000.00"))
                .build());

        criteriaRepository.save(TierCriteria.builder()
                .tier(platinum)
                .criteriaType(CriteriaType.COHORT_MEMBERSHIP)
                .thresholdValue(new BigDecimal("3"))
                .requiredCohort(CohortType.VIP)
                .build());

        return platinum;
    }

    private void seedUsers() {
        userRepository.save(User.builder()
                .name("Alice Kumar")
                .email("alice@example.com")
                .phoneNumber("+91-9876543210")
                .cohort(CohortType.REGULAR)
                .build());

        userRepository.save(User.builder()
                .name("Bob Sharma")
                .email("bob@example.com")
                .phoneNumber("+91-9876543211")
                .cohort(CohortType.PREMIUM)
                .build());

        userRepository.save(User.builder()
                .name("Carol Mehta")
                .email("carol@example.com")
                .phoneNumber("+91-9876543212")
                .cohort(CohortType.VIP)
                .build());

        userRepository.save(User.builder()
                .name("David Patel")
                .email("david@example.com")
                .phoneNumber("+91-9876543213")
                .cohort(CohortType.ENTERPRISE)
                .build());

        log.info("Seeded 4 demo users (IDs 1–4): Alice (REGULAR), Bob (PREMIUM), Carol (VIP), David (ENTERPRISE)");
    }
}
