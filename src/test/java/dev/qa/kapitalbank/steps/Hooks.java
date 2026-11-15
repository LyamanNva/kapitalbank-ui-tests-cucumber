package dev.qa.kapitalbank.steps;

import dev.qa.kapitalbank.driver.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;

public class Hooks {

    @Before
    public void startBrowser() {
        DriverManager.start();
    }

    @After
    public void attachEvidenceAndQuit(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                byte[] screenshot = ((TakesScreenshot) DriverManager.get()).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Screenshot at failure");
                scenario.log("URL at failure: " + DriverManager.get().getCurrentUrl());
            }
        } catch (WebDriverException e) {
            scenario.log("Could not capture a screenshot: " + e.getMessage());
        } finally {
            DriverManager.quit();
        }
    }
}
