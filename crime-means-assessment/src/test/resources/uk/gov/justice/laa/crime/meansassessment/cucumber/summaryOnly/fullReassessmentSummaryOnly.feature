Feature: Stateless API Demo
  Demo of steps for Stateless API

#  Scenario: Full Assessment - case type "Summary only"
#    Given A full assessment
#    And A case type of "SUMMARY_ONLY"
#    And A mag court outcome of "RESOLVED_IN_MAGS"
#    And The applicant has a partner
#    And The partner has income of 2952.00 and frequency "MONTHLY"
#    And The partner has outgoings of 2935.60 with frequency "MONTHLY"
#    When I call the stateless CMA endpoint
#    Then I expect the initial outcome to be "FULL"

  Scenario Outline: Full Assessment - case type "Summary only" - examples
    Given The applicant details
      | caseType   | magCourtOutcome   | hasAPartner   | frequency   | income   | outgoings   | partnerIncome   | partnerOutgoings   |
      | <caseType> | <magCourtOutcome> | <hasAPartner> | <frequency> | <income> | <outgoings> | <partnerIncome> | <partnerOutgoings> |
    And children with ages
      | 0-1   | 2-4  | 5-7  | 8-10  | 11-12  | 13-15  | 16-18  |
      | <0-1> |<2-4> |<5-7> |<8-10> |<11-12> |<13-15> |<16-18> |
    When I call the stateless CMA endpoint
    Then I expect the result to be
      | imaResult   | imaReason   | fullAssessmentAvailable   | fmaResult   | fmaReason   | totalAggregatedIncome   | adjustedIncome   | adjustedLivingAllowance   | totalAggregatedExpense   | totalAnnualDisposableIncome   |
      | <imaResult> | <imaReason> | <fullAssessmentAvailable> | <fmaResult> | <fmaReason> | <totalAggregatedIncome> | <adjustedIncome> | <adjustedLivingAllowance> | <totalAggregatedExpense> | <totalAnnualDisposableIncome> |
    Examples:
      | description                                | hasAPartner | caseType     | magCourtOutcome  | 0-1 | 2-4 | 5-7 | 8-10 | 11-12 | 13-15 | 16-18 | frequency | income  | outgoings     | partnerIncome | partnerOutgoings | imaResult | imaReason                                              | fullAssessmentAvailable | totalAggregatedIncome | adjustedIncome | fmaResult | fmaReason                             | adjustedLivingAllowance | totalAggregatedExpense | totalAnnualDisposableIncome |
      | 150 Pass Full Means + PAI Pass Full Means  | true        | SUMMARY_ONLY |                  |  0  |  0  |  0  | 0    | 0     | 0     | 0     | MONTHLY   |         |               | 2952.00       | 2952.00          | FULL      | Gross income in between the upper and lower thresholds | true                    | 35424.00              | 21600.00       | PASS      | Disposable income below the threshold | 9308.64                 | 44732.64               | -9308.64                    |
      | 152 Fail Full Means + PAI Pass Full Means  | true        | SUMMARY_ONLY | RESOLVED_IN_MAGS |  1  |  0  |  0  | 0    | 0     | 0     | 0     | MONTHLY   | 1611.00 | 45.00         | 1611.00       |                  | FULL      | Gross income in between the upper and lower thresholds | true                    | 38664.00              | 21600.00       | FAIL      | Disposable income above the threshold | 10160.04                | 10700.04               | 27963.96                    |
      | 158 Pass Full Means + PAI Fail Full Means  | false       | SUMMARY_ONLY | RESOLVED_IN_MAGS |  0  |  0  |  0  | 0    | 0     | 0     | 0     | MONTHLY   | 1800.00 | 1800.00       |               |                  | FULL      | Gross income in between the upper and lower thresholds | true                    | 21600.00              | 21600.00       | PASS      | Disposable income below the threshold | 5676.00                 | 27276.00               | -5676.00                    |
      | 160 Fail Full Means + PAI Fail Full Means  | true        | SUMMARY_ONLY | RESOLVED_IN_MAGS |  0  |  0  |  0  | 0    | 0     | 0     | 0     | MONTHLY   | 1476.00 | 45.00         | 1476.00       |                  | FULL      | Gross income in between the upper and lower thresholds | true                    | 35424.00              | 21600.00       | FAIL      | Disposable income above the threshold | 9308.64                 | 9848.64                | 25575.36                    |
      | 166 Pass Full Means + CIFC Pass Full Means | true        | SUMMARY_ONLY | RESOLVED_IN_MAGS |  0  |  0  |  0  | 0    | 0     | 0     | 0     | MONTHLY   |         |               | 2952.00       | 2952.00          | FULL      | Gross income in between the upper and lower thresholds | true                    | 35424.00              | 21600.00       | PASS      | Disposable income below the threshold | 9308.64                 | 44732.64               | -9308.64                    |
      | 168 Fail Full Means + CIFC Pass Full Means | true        | SUMMARY_ONLY | RESOLVED_IN_MAGS |  0  |  0  |  0  | 0    | 0     | 0     | 0     | MONTHLY   | 1476.00 | 45.00         | 1476.00       |                  | FULL      | Gross income in between the upper and lower thresholds | true                    | 35424.00              | 21600.00       | FAIL      | Disposable income above the threshold | 9308.64                 | 9848.64                | 25575.36                    |
      | 174 Pass Full Means + CIFC Fail Full Means | true        | SUMMARY_ONLY | RESOLVED_IN_MAGS |  0  |  0  |  0  | 0    | 0     | 0     | 0     | MONTHLY   |         |               | 2952.00       | 2952.00          | FULL      | Gross income in between the upper and lower thresholds | true                    | 35424.00              | 21600.00       | PASS      | Disposable income below the threshold | 9308.64                 | 44732.64               | -9308.64                    |
      | 176 Fail Full Means + CIFC Fail Full Means | true        | SUMMARY_ONLY | RESOLVED_IN_MAGS |  0  |  0  |  0  | 0    | 0     | 0     | 0     | MONTHLY   | 1476.00 | 45.00         | 1476.00       |                  | FULL      | Gross income in between the upper and lower thresholds | true                    | 35424.00              | 21600.00       | FAIL      | Disposable income above the threshold | 9308.64                 | 9848.64                | 25575.36                    |
