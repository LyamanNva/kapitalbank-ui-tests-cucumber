@currency
Feature: Currency rates and converter
  The home page shows today's buy and sell rates and a converter beside them.
  Whatever the converter shows must agree with the rates in that table.

  The converter has two modes behind the labels "Mən satıram" and "Mən alıram".
  In the default mode an AZN amount is converted at the bank's sell rate,
  in the second mode at the bank's buy rate.

  Background:
    Given the Kapital Bank home page is open

  @smoke
  Scenario Outline: The rates table lists <currency>
    Then the rates table should list a buy and a sell rate for "<currency>"
    And the buy rate for "<currency>" should not exceed its sell rate

    Examples:
      | currency |
      | USD      |
      | EUR      |

  @smoke
  Scenario: Default mode converts AZN at the sell rate
    When I enter 100 into the currency converter
    Then the converted "USD" amount should equal the entered amount divided by its sell rate
    And the converted "EUR" amount should equal the entered amount divided by its sell rate

  @regression
  Scenario Outline: Default mode converts <amount> AZN at the sell rate
    When I enter <amount> into the currency converter
    Then the converted "USD" amount should equal the entered amount divided by its sell rate
    And the converted "EUR" amount should equal the entered amount divided by its sell rate

    Examples:
      | amount |
      | 1      |
      | 250    |
      | 9999   |

  @regression
  Scenario Outline: Second mode converts <amount> AZN at the buy rate
    When I switch the currency converter to its second mode
    And I enter <amount> into the currency converter
    Then the converted "USD" amount should equal the entered amount divided by its buy rate
    And the converted "EUR" amount should equal the entered amount divided by its buy rate

    Examples:
      | amount |
      | 1      |
      | 250    |
      | 9999   |

  @regression
  Scenario: Second mode converts USD to AZN at the USD sell rate
    When I switch the currency converter to its second mode
    And I choose "USD" as the currency converter source
    And I enter 250 into the currency converter
    Then the converted AZN amount should equal the entered amount multiplied by the "USD" sell rate

  @regression
  Scenario: An empty amount converts to zero
    When I clear the currency converter amount
    Then every converted amount should be zero
