Feature: Stateless API Demo
  Demo of steps for Stateless API

  Scenario Outline: Full Assessment - case type "Indicatable" - examples
    Given The applicant details
      | caseType   | hasAPartner   |  frequency   | income   | outgoings   |
      | <caseType> | <hasAPartner> |  <frequency> | <income> | <outgoings> |
    And children with ages
      | 0-1   | 2-4  | 5-7  | 8-10  | 11-12  | 13-15  | 16-18  |
      | <0-1> |<2-4> |<5-7> |<8-10> |<11-12> |<13-15> |<16-18> |
    When I call the stateless CMA endpoint
    Then I expect the result to be
      | imaResult   | grossIncome   | fmaResult   | fmaReason   | adjustedLivingAllowance   | totalAggregatedExpense   | totalAnnualDisposableIncome   |
      | <imaResult> | <grossIncome> | <fmaResult> | <fmaReason> | <adjustedLivingAllowance> | <totalAggregatedExpense> | <totalAnnualDisposableIncome> |
    Examples:
      | description                                     | hasAPartner | frequency | income  | outgoings | 0-1 | 2-4 | 5-7 | 8-10 | 11-12 | 13-15 | 16-18 | imaResult | fmaResult | fmaReason                             | caseType   | grossIncome | housingCostsTotal | expenditureTotal | totalAggregatedExpense | totalAnnualDisposableIncome | adjustedLivingAllowance |
      | 63 Pass Full Means + PAI Pass Full Means        | false       | MONTHLY   | 1800.00 | 1800.00   | 0   | 0   | 0   | 0    | 0     | 0     | 0     | FULL      | PASS      | Disposable income below the threshold | INDICTABLE | 21480.00    | 21480.00          | 21480.00         | 27276.00             | -5676.00                    | 5676.00                 |
      | 64 Fail Initial Means + PAI Pass Full Means     | true        | ANNUALLY  | 60000.00| 20000.00  | 0   | 0   | 0   | 0    | 1     | 0     | 0     | FAIL      | FAIL      | Disposable income above the threshold | INDICTABLE | 21480.00    | 21480.00          | 21480.00         | 31635.80             | 28364.20                    | 11635.80                |