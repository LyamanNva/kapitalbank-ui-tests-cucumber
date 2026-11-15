package dev.qa.kapitalbank.pages;

import dev.qa.kapitalbank.utils.Money;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** "Valyuta kalkulyatoru" block on the home page: the rates table and the converter next to it. */
public class CurrencyWidget extends BasePage {

    /** Bank's buy and sell price of one unit of a currency, in AZN. */
    public record Rate(BigDecimal buy, BigDecimal sell) {
    }

    private static final By RATE_ROWS = By.cssSelector("section.main-currency tbody.show-min-986 tr");
    private static final By BUY_CELL = By.cssSelector("td.currency_type_purchase");

    private static final By AMOUNT_FIELD = By.id("currency_amount");
    /** Hidden checkbox behind the two mode labels: unchecked is the default mode. */
    private static final By MODE_CHECKBOX = By.id("currency_type");
    private static final By MODE_LABELS = By.cssSelector(".currency__type__radio label");
    private static final By CURRENCY_OPENER = By.cssSelector("#currency_selector_list .selector__opener");
    private static final By CURRENCY_OPTIONS = By.cssSelector("#currency_selector_list .selector__list-option");
    private static final By SELECTED_CURRENCY = By.cssSelector("#currency_selector_list .selected_value");
    private static final By RESULT_ROWS = By.cssSelector("#currency_table_body tr");

    /** Rates table as shown to the customer, keyed by currency code (USD, EUR). */
    public Map<String, Rate> rates() {
        wait.until(d -> !d.findElements(BUY_CELL).isEmpty());
        Map<String, Rate> rates = new LinkedHashMap<>();
        for (WebElement row : all(RATE_ROWS)) {
            if (row.findElements(BUY_CELL).isEmpty()) {
                continue; // header row
            }
            List<WebElement> cells = row.findElements(By.tagName("td"));
            rates.put(text(cells.get(0)), new Rate(Money.parse(text(cells.get(1))), Money.parse(text(cells.get(2)))));
        }
        return rates;
    }

    public void typeAmount(String amount) {
        replaceText(visible(AMOUNT_FIELD), amount);
    }

    /** Clicks the second mode label once, which ticks the hidden checkbox. */
    public void switchToSecondMode() {
        if (!present(MODE_CHECKBOX).isSelected()) {
            click(all(MODE_LABELS).get(1));
            wait.until(d -> d.findElement(MODE_CHECKBOX).isSelected());
        }
    }

    public void chooseCurrency(String code) {
        click(visible(CURRENCY_OPENER));
        WebElement option = wait.until(d -> d.findElements(CURRENCY_OPTIONS).stream()
                .filter(candidate -> text(candidate).equals(code) && candidate.isDisplayed())
                .findFirst()
                .orElse(null));
        option.click();
        wait.until(d -> text(d.findElement(SELECTED_CURRENCY)).equals(code));
    }

    /** Converter output: currency code -> amount, in the order shown. */
    public Map<String, BigDecimal> results() {
        Map<String, BigDecimal> results = new LinkedHashMap<>();
        for (WebElement row : all(RESULT_ROWS)) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 2) {
                results.put(text(cells.get(1)), Money.parse(text(cells.get(0))));
            }
        }
        return results;
    }
}
