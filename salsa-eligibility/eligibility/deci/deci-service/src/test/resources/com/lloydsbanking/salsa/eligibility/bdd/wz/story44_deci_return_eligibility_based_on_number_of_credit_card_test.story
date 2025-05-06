Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for number of credit cards customer holds

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for Credit Card which has less than 2 Credit Card products

Given customer has less than 2 credit cards
And rule is CR031
When the UI calls DECI for product
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for Credit Card which have more than thresholdCount Credit Card products

Given customer has more than thresholdCount credit cards
And rule is CR031
When the UI calls DECI for product
Then DECI evaluates eligibility to false and returns error condition for customer having 2 or more credit cards.