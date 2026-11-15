package dev.qa.kapitalbank.pages;

import dev.qa.kapitalbank.config.Config;
import dev.qa.kapitalbank.driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/** Shared browser helpers. Page classes hold locators and actions; assertions live in the steps. */
public abstract class BasePage {

    protected final WebDriver driver = DriverManager.get();
    protected final WebDriverWait wait = new WebDriverWait(driver, Config.timeout());

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement present(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected List<WebElement> all(By locator) {
        return driver.findElements(locator);
    }

    /** Text as written in the DOM, whitespace-normalised; works for elements hidden by CSS too. */
    protected static String text(WebElement element) {
        String raw = element.getDomProperty("textContent");
        return raw == null ? "" : raw.replace(' ', ' ').trim().replaceAll("\\s+", " ");
    }

    protected static String value(WebElement element) {
        String raw = element.getDomProperty("value");
        return raw == null ? "" : raw.trim();
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    /**
     * Replaces a field's content the way a user does: select everything, then type.
     * WebElement.clear() is avoided on purpose: it fires a change event on the empty field,
     * and the calculators react to that by snapping the value to their minimum.
     */
    protected void replaceText(WebElement field, String newText) {
        scrollIntoView(field);
        ((JavascriptExecutor) driver).executeScript("arguments[0].focus(); arguments[0].select();", field);
        if (newText.isEmpty()) {
            field.sendKeys(Keys.BACK_SPACE);
        } else {
            field.sendKeys(newText);
        }
    }

    protected void click(WebElement element) {
        scrollIntoView(element);
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }
}
