package dev.qa.kapitalbank.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Number parsing and the financial formulas used as test oracles. */
public final class Money {

    /** The site shows two decimals; one unit of the last digit absorbs its rounding mode. */
    public static final BigDecimal TOLERANCE = new BigDecimal("0.01");

    private Money() {
    }

    /** "80 000", "10.9 %", "59 ay", "3417.01" -> BigDecimal. */
    public static BigDecimal parse(String text) {
        String digits = text == null ? "" : text.replaceAll("[^0-9.]", "");
        if (digits.isEmpty()) {
            throw new IllegalArgumentException("No number in text: '" + text + "'");
        }
        return new BigDecimal(digits);
    }

    /**
     * Monthly annuity payment: P * r / (1 - (1 + r)^-n), where r is the monthly rate.
     * Example: 10 000 AZN, 15 % a year, 3 months -> 3417.01.
     */
    public static BigDecimal annuityPayment(BigDecimal principal, BigDecimal annualRatePercent, int months) {
        if (months <= 0) {
            throw new IllegalArgumentException("months must be positive");
        }
        double p = principal.doubleValue();
        double r = annualRatePercent.doubleValue() / 100 / 12;
        double payment = r == 0 ? p / months : p * r / (1 - Math.pow(1 + r, -months));
        return round(payment);
    }

    public static BigDecimal round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
