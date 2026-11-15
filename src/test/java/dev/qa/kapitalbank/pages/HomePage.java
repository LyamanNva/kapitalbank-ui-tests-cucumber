package dev.qa.kapitalbank.pages;

import dev.qa.kapitalbank.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Optional;

/** Header, main menu and language switcher of the home page. */
public class HomePage extends BasePage {

    private static final By MAIN_HEADING = By.cssSelector("h1.seo-h1");
    /** Main-menu items and language codes share these classes; they are told apart by text. */
    private static final By HEADER_LINKS = By.cssSelector("a.chable.text-uppercase");
    private static final By LOAN_PAYMENT = By.id("monthly_payment");

    public void open() {
        driver.get(Config.baseUrl());
        // The page is usable once its scripts have filled the loan calculator result.
        wait.until(d -> !text(d.findElement(LOAN_PAYMENT)).isEmpty());
    }

    public String title() {
        return driver.getTitle();
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    public String mainHeading() {
        return text(present(MAIN_HEADING));
    }

    /** href of the main-menu item with exactly this label, e.g. "KARTLAR". */
    public String menuLinkTarget(String label) {
        return headerLinks().stream()
                .filter(link -> text(link).equals(label))
                .map(link -> link.getDomProperty("href"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Main menu has no item '" + label + "'"));
    }

    /** The header renders the switcher twice (desktop and mobile); click the one on screen. */
    public void switchLanguage(String code) {
        Optional<WebElement> link = headerLinks().stream()
                .filter(candidate -> text(candidate).equalsIgnoreCase(code))
                .filter(WebElement::isDisplayed)
                .findFirst();
        click(link.orElseThrow(() -> new AssertionError("No visible language link '" + code + "'")));
    }

    private List<WebElement> headerLinks() {
        wait.until(d -> !d.findElements(HEADER_LINKS).isEmpty());
        return all(HEADER_LINKS);
    }
}
