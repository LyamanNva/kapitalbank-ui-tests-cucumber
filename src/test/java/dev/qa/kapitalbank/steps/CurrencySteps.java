package dev.qa.kapitalbank.steps;

import dev.qa.kapitalbank.pages.CurrencyWidget;
import dev.qa.kapitalbank.pages.CurrencyWidget.Rate;
import dev.qa.kapitalbank.utils.Money;
import dev.qa.kapitalbank.utils.Retry;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * The expected converter values are never hard-coded: exchange rates change daily, so every
 * expectation is calculated from the rates table the same page shows to the customer.
 */
public class CurrencySteps {

    private BigDecimal enteredAmount;

    private CurrencyWidget widget() {
        return new CurrencyWidget();
    }

    @Then("the rates table should list a buy and a sell rate for {string}")
    public void ratesTableShouldList(String currency) {
        Rate rate = rateOf(currency);
        assertThat(rate.buy()).as("%s buy rate", currency).isPositive();
        assertThat(rate.sell()).as("%s sell rate", currency).isPositive();
    }

    @Then("the buy rate for {string} should not exceed its sell rate")
    public void buyRateShouldNotExceedSellRate(String currency) {
        Rate rate = rateOf(currency);
        assertThat(rate.buy()).as("%s buy rate vs sell rate %s", currency, rate.sell())
                .isLessThanOrEqualTo(rate.sell());
    }

    @When("I enter {bigdecimal} into the currency converter")
    public void enterAmount(BigDecimal amount) {
        enteredAmount = amount;
        widget().typeAmount(amount.toPlainString());
    }

    @When("I clear the currency converter amount")
    public void clearAmount() {
        enteredAmount = BigDecimal.ZERO;
        widget().typeAmount("");
    }

    @When("I switch the currency converter to its second mode")
    public void switchToSecondMode() {
        widget().switchToSecondMode();
    }

    @When("I choose {string} as the currency converter source")
    public void chooseSourceCurrency(String currency) {
        widget().chooseCurrency(currency);
    }

    @Then("the converted {string} amount should equal the entered amount divided by its sell rate")
    public void convertedEqualsAmountDividedBySellRate(String currency) {
        double expected = enteredAmount.doubleValue() / rateOf(currency).sell().doubleValue();
        assertConverted(currency, expected);
    }

    @Then("the converted {string} amount should equal the entered amount divided by its buy rate")
    public void convertedEqualsAmountDividedByBuyRate(String currency) {
        double expected = enteredAmount.doubleValue() / rateOf(currency).buy().doubleValue();
        assertConverted(currency, expected);
    }

    @Then("the converted AZN amount should equal the entered amount multiplied by the {string} sell rate")
    public void convertedAznEqualsAmountTimesSellRate(String currency) {
        double expected = enteredAmount.doubleValue() * rateOf(currency).sell().doubleValue();
        assertConverted("AZN", expected);
    }

    @Then("every converted amount should be zero")
    public void everyConvertedAmountShouldBeZero() {
        CurrencyWidget widget = widget();
        Retry.eventually(() -> {
            Map<String, BigDecimal> results = widget.results();
            assertThat(results).as("converter results").isNotEmpty();
            assertThat(results.values()).allSatisfy(amount -> assertThat(amount).isEqualByComparingTo(BigDecimal.ZERO));
        });
    }

    private Rate rateOf(String currency) {
        Map<String, Rate> rates = widget().rates();
        assertThat(rates).as("rates table").containsKey(currency);
        return rates.get(currency);
    }

    private void assertConverted(String currency, double expected) {
        CurrencyWidget widget = widget();
        BigDecimal expectedRounded = Money.round(expected);
        Retry.eventually(() -> {
            Map<String, BigDecimal> results = widget.results();
            assertThat(results).as("converter results").containsKey(currency);
            assertThat(results.get(currency))
                    .as("converted %s amount for %s", currency, enteredAmount.toPlainString())
                    .isCloseTo(expectedRounded, within(Money.TOLERANCE));
        });
    }
}
