# Kapital Bank public site: UI test automation (BDD)

Behaviour-driven UI tests for the public home page of [kapitalbank.az](https://www.kapitalbank.az/):
the cash loan calculator, the currency rates and converter, and the main navigation.

This is an independent portfolio project. It is not an official Kapital Bank repository.

## Stack

| Purpose | Tool |
|---|---|
| Language, build | Java 17, Maven |
| Browser automation | Selenium WebDriver 4 (Chrome, Firefox) |
| BDD | Cucumber 7, Gherkin |
| Runner | TestNG |
| Assertions | AssertJ |
| Reporting | Cucumber HTML report, Allure results |
| CI | GitHub Actions |

## What is covered

| Feature file | Scenarios | What it checks |
|---|---|---|
| `loan_calculator.feature` | 15 + 2 known bugs | Monthly payment against the annuity formula, limits of amount and term |
| `currency_converter.feature` | 11 | Rates table sanity, converter results against the rates shown on the page |
| `navigation.feature` | 9 | Title and heading, main menu targets, language switch |

Two defects found on the live site are documented in [docs/BUGS.md](docs/BUGS.md).

## Design decisions

**Calculated expectations instead of hard-coded ones.** Exchange rates change every day, so no
expected converter value is written in a feature file. Each expectation is calculated from the
rates table on the same page. The loan payment is checked against the annuity formula
`P * r / (1 - (1 + r)^-n)` implemented in `Money.annuityPayment`.

**Read-only against production.** The suite only reads public pages. It never opens or submits an
application form: the "Sifariş et" button is verified by its link target, not by clicking it.
Scenarios run one at a time, so the traffic equals one visitor.

**No fixed sleeps.** The calculators recalculate asynchronously. `Retry.eventually` polls the
assertion itself until it passes or the timeout expires.

**Known defects stay visible.** Scenarios for open defects are tagged `@known-bug`, written as the
correct behaviour and excluded from the default run, so the main suite stays green and the
defect scenarios pass on their own once the fix is released.

**Page Object Model.** Locators and actions live in `pages`, assertions in `steps`, formulas in
`utils`. Step classes hold no locators.

## Test sets

Tags follow the smoke / regression model:

| Tag expression | Set | When to run |
|---|---|---|
| `@smoke` | Critical health checks, under a minute | After every deploy |
| `not @known-bug` (default) | Full regression | Before a release, nightly |
| `@boundary` | Limit checks only | When calculator rules change |
| `@known-bug` | Open defects | To confirm a fix |

## Running

Requirements: JDK 17 or newer, Maven, Chrome or Firefox. Selenium Manager downloads the driver.

```bash
mvn test                                         # full regression, headless Chrome
mvn test -Dcucumber.filter.tags="@smoke"         # smoke set
mvn test -Dcucumber.filter.tags="@loan-calculator and not @known-bug"
mvn test -Dheadless=false                        # watch the browser
mvn test -Dbrowser=firefox
```

Settings are in `src/test/resources/config.properties`; any key can be overridden with `-D`.

## Reports

- `target/cucumber-report.html`: open in a browser. Failed scenarios include a screenshot.
- `target/allure-results`: with the Allure CLI installed, run `allure serve target/allure-results`.

## Project layout

```
src/test/java/dev/qa/kapitalbank
  config/   Config            properties with -D overrides
  driver/   DriverManager     one browser per thread
  pages/    HomePage, LoanCalculator, CurrencyWidget
  steps/    Hooks and step definitions
  utils/    Money (parsing, formulas), Retry (polling assertions)
  runner/   TestRunner        TestNG entry point
src/test/resources
  features/                   Gherkin scenarios
  config.properties
docs/BUGS.md                  defects found on the live site
.github/workflows/ui-tests.yml
```
