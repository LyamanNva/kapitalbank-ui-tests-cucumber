@navigation
Feature: Home page navigation
  A visitor reaches the main product sections and switches the site language.

  Background:
    Given the Kapital Bank home page is open

  @smoke
  Scenario: The home page identifies the bank
    Then the page title should contain "Kapital Bank"
    And the main heading should be "Kapital Bank - Birinci Bank"

  @regression
  Scenario Outline: Main menu item <item> points to its section
    Then the main menu item "<item>" should link to a URL containing "<target>"

    Examples:
      | item             | target           |
      | KARTLAR          | /cards           |
      | KREDİTLƏR        | /loans           |
      | ƏMANƏTLƏR        | /deposits        |
      | PUL KÖÇÜRMƏLƏRİ  | /money-transfers |
      | ONLAYN XİDMƏTLƏR | /online-order    |
      | KAMPANİYALAR     | /kampaniyalar    |

  @regression
  Scenario Outline: Switching the language to <language> opens that version of the site
    When I switch the site language to "<language>"
    Then the current URL should end with "<path>"

    Examples:
      | language | path  |
      | en       | /en   |
      | ru       | /ru   |
