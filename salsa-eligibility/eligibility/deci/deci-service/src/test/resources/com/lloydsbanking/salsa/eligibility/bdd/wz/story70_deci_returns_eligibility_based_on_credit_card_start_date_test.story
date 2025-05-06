Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for credit card based
on credit card start date

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for customer having absolute difference from current date is greater than threshold

Given customer does not hold a credit card opened in last
And rule is CR064
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for customer absolute difference from current date and start date  is less  than threshold


Given customer holds a credit card opened in last
And rule is CR064
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns customer holds a credit card opened in last

