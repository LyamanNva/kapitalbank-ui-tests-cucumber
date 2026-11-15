@loan-calculator
Feature: Cash loan calculator
  A visitor estimates the monthly payment of a cash loan before applying.
  The payment must follow the annuity formula for the amount, yearly rate and
  term the calculator displays, and out-of-range input must snap to the limits.

  Background:
    Given the Kapital Bank home page is open

  @smoke
  Scenario: Default values produce an annuity payment
    Then the monthly payment should match the annuity formula for the displayed values

  @smoke
  Scenario: The order button leads to the online loan application
    Then the order button should lead to "ccl.birbank.az"

  @regression
  Scenario Outline: Payment is recalculated for <amount> AZN over <months> months
    When I set the loan amount to <amount> AZN
    And I set the loan term to <months> months
    Then the monthly payment should match the annuity formula for <amount> AZN over <months> months at the displayed rate

    Examples: lower limits, typical values, upper limits
      | amount | months |
      | 300    | 3      |
      | 5000   | 12     |
      | 10000  | 3      |
      | 25000  | 36     |
      | 50000  | 59     |

  @regression @boundary
  Scenario Outline: A term of <typed> months snaps to <shown>
    When I set the loan term to <typed> months
    Then the loan term field should show <shown>
    And the monthly payment should match the annuity formula for the displayed values

    Examples:
      | typed | shown |
      | 1     | 3     |
      | 2     | 3     |
      | 3     | 3     |
      | 59    | 59    |
      | 60    | 59    |
      | 120   | 59    |

  @regression @boundary
  Scenario Outline: An amount of <typed> AZN below the minimum snaps to 300
    When I set the loan amount to <typed> AZN
    Then the loan amount field should show 300
    And the monthly payment should match the annuity formula for the displayed values

    Examples:
      | typed |
      | 1     |
      | 299   |

  # Open defects. Excluded from the default run; see docs/BUGS.md.
  # They are written as the correct behaviour, so they turn green when the defect is fixed.

  @known-bug @BUG-001
  Scenario: Typing an interest rate recalculates the payment at that rate
    When I type 18 into the interest rate field
    Then the monthly payment should match the annuity formula for 10000 AZN over 3 months at 18 percent

  @known-bug @BUG-002
  Scenario: The maximum amount shown under the slider can be entered
    When I set the loan amount to the maximum shown under the amount slider
    Then the loan amount field should show the maximum shown under the amount slider
