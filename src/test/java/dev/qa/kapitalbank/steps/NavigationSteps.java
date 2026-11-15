package dev.qa.kapitalbank.steps;

import dev.qa.kapitalbank.pages.HomePage;
import dev.qa.kapitalbank.utils.Retry;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class NavigationSteps {

    private HomePage home() {
        return new HomePage();
    }

    @Given("the Kapital Bank home page is open")
    public void homePageIsOpen() {
        home().open();
    }

    @Then("the page title should contain {string}")
    public void pageTitleShouldContain(String expected) {
        assertThat(home().title()).contains(expected);
    }

    @Then("the main heading should be {string}")
    public void mainHeadingShouldBe(String expected) {
        assertThat(home().mainHeading()).isEqualTo(expected);
    }

    @Then("the main menu item {string} should link to a URL containing {string}")
    public void menuItemShouldLinkTo(String label, String urlPart) {
        assertThat(home().menuLinkTarget(label))
                .as("target of main menu item '%s'", label)
                .contains(urlPart);
    }

    @When("I switch the site language to {string}")
    public void switchLanguage(String code) {
        home().switchLanguage(code);
    }

    @Then("the current URL should end with {string}")
    public void currentUrlShouldEndWith(String suffix) {
        Retry.eventually(() -> assertThat(home().currentUrl()).endsWith(suffix));
    }
}
