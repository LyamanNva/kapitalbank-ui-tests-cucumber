package dev.qa.kapitalbank.driver;

import dev.qa.kapitalbank.config.Config;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * One browser per thread. Selenium Manager (built into Selenium 4) downloads the
 * matching driver binary, so no driver path has to be configured.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver get() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("Browser is not started; the @Before hook did not run");
        }
        return driver;
    }

    public static void start() {
        WebDriver driver = create();
        driver.manage().window().setSize(new Dimension(Config.windowWidth(), Config.windowHeight()));
        DRIVER.set(driver);
    }

    public static void quit() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }

    private static WebDriver create() {
        String browser = Config.browser();
        switch (browser) {
            case "chrome":
                ChromeOptions chrome = new ChromeOptions();
                if (Config.headless()) {
                    chrome.addArguments("--headless=new");
                }
                chrome.addArguments(
                        "--window-size=" + Config.windowWidth() + "," + Config.windowHeight(),
                        "--disable-notifications",
                        "--no-sandbox",
                        "--disable-dev-shm-usage");
                return new ChromeDriver(chrome);
            case "firefox":
                FirefoxOptions firefox = new FirefoxOptions();
                if (Config.headless()) {
                    firefox.addArguments("-headless");
                }
                return new FirefoxDriver(firefox);
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser + " (use chrome or firefox)");
        }
    }
}
