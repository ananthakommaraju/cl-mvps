Meta:

Narrative:
Determine eligible customer instruction service should check eligibility based
on credit card

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for threshold equal to ELIGIBLE

Given customer is eligible to apply for a credit card
And rule is CR048
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false threshold equal to INELIGIBLE

Given customer is not eligible to apply for a credit card
And rule is CR048
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns customer not eligible to apply for a credit card



