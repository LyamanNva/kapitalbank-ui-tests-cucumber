package dev.qa.kapitalbank.pages;

import dev.qa.kapitalbank.utils.Money;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;

/**
 * "Nağd pul krediti" calculator on the home page.
 * Each parameter has a text field the user types into and a hidden input that holds the
 * slider's real value; the monthly payment is calculated from the hidden inputs.
 */
public class LoanCalculator extends BasePage {

    private static final By AMOUNT_FIELD = By.id("current_loan_amount");
    private static final By RATE_FIELD = By.id("current_loan_percent");
    private static final By TERM_FIELD = By.id("current_month_amount");

    private static final By AMOUNT_SLIDER = By.id("loan_amount");
    private static final By RATE_SLIDER = By.id("loan_percent");
    private static final By TERM_SLIDER = By.id("loan_month");

    private static final By MONTHLY_PAYMENT = By.id("monthly_payment");
    private static final By ORDER_BUTTON = By.cssSelector(".calculate-result a.btn");
    private static final By AMOUNT_MAX_LABEL = By.xpath(
            "//input[@id='current_loan_amount']/ancestor::div[contains(@class,'range-block')]"
                    + "//div[contains(@class,'range-values')]/span[last()]");

    public void typeAmount(String amount) {
        typeAndCommit(AMOUNT_FIELD, amount);
    }

    public void typeRate(String rate) {
        typeAndCommit(RATE_FIELD, rate);
    }

    public void typeTerm(String months) {
        typeAndCommit(TERM_FIELD, months);
    }

    public BigDecimal amountShown() {
        return Money.parse(value(present(AMOUNT_FIELD)));
    }

    public BigDecimal rateShown() {
        return Money.parse(value(present(RATE_FIELD)));
    }

    public int termShown() {
        return Money.parse(value(present(TERM_FIELD))).intValueExact();
    }

    /** What the calculator really uses, as opposed to what the text fields display. */
    public String sliderState() {
        return String.format("slider amount=%s, rate=%s, term=%s",
                value(present(AMOUNT_SLIDER)), value(present(RATE_SLIDER)), value(present(TERM_SLIDER)));
    }

    public BigDecimal monthlyPayment() {
        return Money.parse(text(present(MONTHLY_PAYMENT)));
    }

    /** Upper limit printed under the amount slider, e.g. "80 000". */
    public BigDecimal advertisedMaxAmount() {
        return Money.parse(text(present(AMOUNT_MAX_LABEL)));
    }

    public String orderButtonTarget() {
        return present(ORDER_BUTTON).getDomProperty("href");
    }

    /** The calculator recalculates on the field's change event, i.e. when focus leaves it. */
    private void typeAndCommit(By fieldLocator, String newText) {
        WebElement field = visible(fieldLocator);
        replaceText(field, newText);
        field.sendKeys(Keys.TAB);
    }
}
