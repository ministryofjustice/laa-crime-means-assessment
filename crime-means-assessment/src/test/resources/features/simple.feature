Feature: Scenarios that reflect the JUnit tests

  Scenario Outline: Examples of PASS/FAIL/FULL on really simple API endpoint
    Given the following applicant details
      | total_income   |
      | <totalIncome> |
    When I POST new assessment request
    Then I GET the assessment details
      | imaResult   |
      | <imaResult> |

    Examples:
      | totalIncome | imaResult   |
      | 12000.0     |   PASS      |
      | 45000.0     |   FULL      |
      | 75000.0     |   FAIL      |
