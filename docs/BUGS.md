# Defects found while building this suite

Both were observed on https://www.kapitalbank.az/ on 15 November 2026 in a Chromium-based
browser. Each has a scenario tagged `@known-bug` in `loan_calculator.feature`; those scenarios
describe the correct behaviour, are excluded from the default run, and turn green once the
defect is fixed.

Run them with:

```
mvn test -Dcucumber.filter.tags="@known-bug"
```

---

## BUG-001: Typing an interest rate changes the loan term instead of the rate

| | |
|---|---|
| Area | Home page, "Nağd pul krediti" calculator |
| Severity | Major: the customer sees a monthly payment that does not match the values on screen |
| Detection stage | PROD |
| Scenario | `@BUG-001` |

**Preconditions:** home page open, calculator at its defaults (10 000 AZN, 15 %, 3 months,
monthly payment 3417.01).

**Steps**
1. Click the "Faiz" (interest rate) field.
2. Replace its value with `18`.
3. Press Tab to leave the field.

**Actual result**
- The rate field shows 18 and the term field still shows 3.
- The monthly payment becomes **623.85**, which is the payment for 10 000 AZN at **15 %** over
  **18 months**.
- The hidden slider inputs confirm it: `#loan_percent` stays 15, `#loan_month` becomes 18.
  The typed rate is applied to the term slider.

**Expected result**
- The payment is recalculated at 18 % for 3 months: **3433.83**.
- The term is not affected by typing a rate.

---

## BUG-002: The amount field does not accept the maximum shown under its slider

| | |
|---|---|
| Area | Home page, "Nağd pul krediti" calculator |
| Severity | Minor: inconsistent limits |
| Detection stage | PROD |
| Scenario | `@BUG-002` |

**Steps**
1. Type an amount above 50 000 into the "Məbləğ" field (90 000 was used).
2. Leave the field.

**Actual result:** the field snaps to **50 000**.

**Expected result:** the limit is the one the page states. The label under the slider reads
"80 000" and the field's own `max` attribute is 80000, so either the field should accept up
to 80 000 or the label and attribute should say 50 000.

**Note:** the snap to 50 000 was observed with 90 000 as input. That 80 000 itself is also
reduced follows from the same limit but should be confirmed by hand before the defect is filed.
