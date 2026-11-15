package dev.qa.kapitalbank.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Default run: everything except scenarios that document open defects.
 * Override from the command line: mvn test -Dcucumber.filter.tags="@smoke"
 *
 * Scenarios run one after another on purpose: the target is a production site,
 * so the suite keeps its traffic at the level of a single visitor.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "dev.qa.kapitalbank.steps",
        tags = "not @known-bug",
        plugin = {
                "pretty",
                "html:target/cucumber-report.html",
                "json:target/cucumber.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        })
public class TestRunner extends AbstractTestNGCucumberTests {
}
