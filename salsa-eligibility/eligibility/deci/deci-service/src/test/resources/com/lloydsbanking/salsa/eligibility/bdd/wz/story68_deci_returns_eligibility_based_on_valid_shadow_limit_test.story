Meta:

Narrative:
Determine eligible customer instruction service should check eligibility based on valid shadow limit.

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for
customers having valid shadow limit

Given customer has valid shadow limit
And rule is CR067
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true


Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for
customers having invalid shadow limit

Given customer has invalid shadow limit
And rule is CR067
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns Customer has invalid shadowLimit