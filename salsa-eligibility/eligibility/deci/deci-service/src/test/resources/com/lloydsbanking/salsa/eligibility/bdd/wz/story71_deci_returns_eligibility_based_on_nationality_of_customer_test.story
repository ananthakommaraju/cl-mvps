Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for ISA based
on nationality of customer

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for nationality not blocked

Given customer nationality is allowed
And rule is CR039
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for nationality is blocked

Given customer nationality is not allowed
And rule is CR039
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns nationality is not allowed

