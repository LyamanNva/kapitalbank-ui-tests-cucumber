package dev.qa.kapitalbank.steps;

import dev.qa.kapitalbank.pages.LoanCalculator;
import dev.qa.kapitalbank.utils.Money;
import dev.qa.kapitalbank.utils.Retry;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class LoanCalculatorSteps {

    private LoanCalculator calculator() {
        return new LoanCalculator();
    }

    @When("I set the loan amount to {int} AZN")
    public void setLoanAmount(int amount) {
        calculator().typeAmount(String.valueOf(amount));
    }

    @When("I set the loan term to {int} months")
    public void setLoanTerm(int months) {
        calculator().typeTerm(String.valueOf(months));
    }

    @When("I type {bigdecimal} into the interest rate field")
    public void typeInterestRate(BigDecimal rate) {
        calculator().typeRate(rate.toPlainString());
    }

    @When("I set the loan amount to the maximum shown under the amount slider")
    public void setLoanAmountToAdvertisedMaximum() {
        LoanCalculator calculator = calculator();
        calculator.typeAmount(calculator.advertisedMaxAmount().toPlainString());
    }

    @Then("the monthly payment should match the annuity formula for the displayed values")
    public void paymentMatchesDisplayedValues() {
        LoanCalculator calculator = calculator();
        Retry.eventually(() -> assertPayment(
                calculator, calculator.amountShown(), calculator.rateShown(), calculator.termShown()));
    }

    @Then("the monthly payment should match the annuity formula for {int} AZN over {int} months at the displayed rate")
    public void paymentMatchesAtDisplayedRate(int amount, int months) {
        LoanCalculator calculator = calculator();
        Retry.eventually(() -> assertPayment(calculator, BigDecimal.valueOf(amount), calculator.rateShown(), months));
    }

    @Then("the monthly payment should match the annuity formula for {int} AZN over {int} months at {bigdecimal} percent")
    public void paymentMatchesAtGivenRate(int amount, int months, BigDecimal rate) {
        LoanCalculator calculator = calculator();
        Retry.eventually(() -> assertPayment(calculator, BigDecimal.valueOf(amount), rate, months));
    }

    @Then("the loan amount field should show {int}")
    public void loanAmountFieldShouldShow(int expected) {
        LoanCalculator calculator = calculator();
        Retry.eventually(() -> assertThat(calculator.amountShown())
                .as("loan amount field")
                .isEqualByComparingTo(BigDecimal.valueOf(expected)));
    }

    @Then("the loan term field should show {int}")
    public void loanTermFieldShouldShow(int expected) {
        LoanCalculator calculator = calculator();
        Retry.eventually(() -> assertThat(calculator.termShown()).as("loan term field").isEqualTo(expected));
    }

    @Then("the loan amount field should show the maximum shown under the amount slider")
    public void loanAmountFieldShouldShowAdvertisedMaximum() {
        LoanCalculator calculator = calculator();
        BigDecimal advertised = calculator.advertisedMaxAmount();
        Retry.eventually(() -> assertThat(calculator.amountShown())
                .as("loan amount field after typing the advertised maximum %s", advertised)
                .isEqualByComparingTo(advertised));
    }

    @Then("the order button should lead to {string}")
    public void orderButtonShouldLeadTo(String urlPart) {
        // Only the link target is checked. The suite never opens the application form:
        // submitting it would create a real loan request.
        assertThat(calculator().orderButtonTarget()).contains(urlPart);
    }

    private static void assertPayment(LoanCalculator calculator, BigDecimal amount, BigDecimal rate, int months) {
        BigDecimal expected = Money.annuityPayment(amount, rate, months);
        assertThat(calculator.monthlyPayment())
                .as("monthly payment for %s AZN, %s %% a year, %s months (%s)",
                        amount.toPlainString(), rate.toPlainString(), months, calculator.sliderState())
                .isCloseTo(expected, within(Money.TOLERANCE));
    }
}
