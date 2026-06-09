# FirstClub Membership API

Tiered subscription membership backend for FirstClub.

## Run

```bash
mvn spring-boot:run
```

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console  
  JDBC URL: `jdbc:h2:mem:firstclubdb` · User: `sa` · Password: *(empty)*

---

## Architecture

```
controller/          REST layer — thin, validation only
service/             Business logic interfaces
  criteria/          Strategy pattern: one @Component per CriteriaType
  impl/              Implementations
entity/              JPA entities with @Version optimistic locking
repository/          Spring Data JPA
config/              DataSeeder, OpenAPI, expiry scheduler
dto/                 Request / Response DTOs (no entity leakage)
enums/               PlanType, TierType, BenefitType, CriteriaType…
exception/           Typed exceptions + global handler
```

### Key Design Decisions

| Concern | Decision |
|---------|----------|
| Tier criteria extensibility | **Strategy pattern** — add `CriteriaType` + `@Component` implementing `TierCriteriaEvaluator`; zero existing code changes |
| Concurrent tier changes | **Optimistic locking** (`@Version` on `UserMembership`) + SERIALIZABLE isolation on upgrade/downgrade |
| Concurrent cancel race | **Pessimistic write lock** (`PESSIMISTIC_WRITE`) for cancel and forced tier updates |
| Benefits per tier | Configurable `TierBenefit` rows — change discount %, free-delivery threshold, add/remove benefit types via DB |
| Tier qualification logic | OR across all active criteria for a tier; tiers evaluated highest → lowest |
| Membership expiry | Background `@Scheduled` job every hour |

---

## Seeded Data

### Plans
| ID | Type | Price | Duration |
|----|------|-------|----------|
| 1 | MONTHLY | ₹299 | 30 days |
| 2 | QUARTERLY | ₹749 | 90 days |
| 3 | YEARLY | ₹2399 | 365 days |

### Tiers
| Tier | Level | Discount | Free Delivery | Criteria (any one) |
|------|-------|----------|---------------|-------------------|
| SILVER | 1 | 5% | Orders ≥ ₹500 | 3+ orders **OR** ₹1k/month **OR** REGULAR cohort |
| GOLD | 2 | 10% | Orders ≥ ₹200 | 10+ orders **OR** ₹5k/month **OR** PREMIUM cohort |
| PLATINUM | 3 | 15% | All orders free | 25+ orders **OR** ₹15k/month **OR** VIP cohort |

### Demo Users
| ID | Name | Email | Cohort |
|----|------|-------|--------|
| 1 | Alice Kumar | alice@example.com | REGULAR |
| 2 | Bob Sharma | bob@example.com | PREMIUM |
| 3 | Carol Mehta | carol@example.com | VIP |
| 4 | David Patel | david@example.com | ENTERPRISE |

---

## API Demo Walkthrough

### 1. Browse available plans
```bash
GET http://localhost:8080/api/v1/plans
```

### 2. Browse tiers with benefits and criteria
```bash
GET http://localhost:8080/api/v1/tiers
```

### 3. Subscribe Alice (user 1) to MONTHLY plan, SILVER tier
```bash
POST http://localhost:8080/api/v1/subscriptions
{
  "userId": 1,
  "planType": "MONTHLY",
  "tierType": "SILVER",
  "autoRenew": true
}
```

### 4. Place an order — discount applied automatically
```bash
POST http://localhost:8080/api/v1/orders
{ "userId": 1, "orderAmount": 1000.00 }
# → finalAmount = ₹950 (5% Silver discount)
# → freeDeliveryApplied = true (order ≥ ₹500 threshold)
```

### 5. Upgrade Alice's tier to GOLD
```bash
PUT http://localhost:8080/api/v1/subscriptions/1/upgrade-tier
{ "targetTier": "GOLD" }
```

### 6. Check membership status and expiry
```bash
GET http://localhost:8080/api/v1/subscriptions/1
```

### 7. Run tier evaluation (auto-compute qualifying tier from criteria)
```bash
POST http://localhost:8080/api/v1/tier-evaluation/1
# Evaluates order count, monthly spend, cohort → updates tier if changed
```

### 8. Preview tier (dry-run, no changes)
```bash
GET http://localhost:8080/api/v1/tier-evaluation/1/preview
```

### 9. Downgrade tier
```bash
PUT http://localhost:8080/api/v1/subscriptions/1/downgrade-tier
{ "targetTier": "SILVER" }
```

### 10. Cancel membership
```bash
DELETE http://localhost:8080/api/v1/subscriptions/1
```

---

## Extending the System

**Add a new CriteriaType** (e.g., `REFERRAL_COUNT`):
1. Add `REFERRAL_COUNT` to `CriteriaType` enum
2. Create `ReferralCountCriteriaEvaluator implements TierCriteriaEvaluator` annotated `@Component`
3. Insert `TierCriteria` rows in DB

No other changes needed — `TierEvaluationServiceImpl` auto-discovers all evaluators.

**Add a new BenefitType** (e.g., `CASHBACK_PERCENTAGE`):
1. Add to `BenefitType` enum
2. Add handling in `OrderServiceImpl.createOrder()` switch
3. Insert `TierBenefit` rows

**Add a new Tier** (e.g., `DIAMOND`):
1. Add to `TierType` enum with level 4
2. Insert `MembershipTier`, `TierBenefit`, and `TierCriteria` rows
