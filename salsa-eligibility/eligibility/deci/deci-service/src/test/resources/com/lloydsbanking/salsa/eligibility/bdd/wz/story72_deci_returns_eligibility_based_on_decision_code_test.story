Meta:

Narrative:
Determine eligible customer instruction service should check eligibility based on CBS decision
code

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for
customers having valid decision code

Given customer has decision code sameAs threshold
And rule is CR062
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true


Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for
customers having invalid decision code

Given customer has decision code differentFrom threshold
And rule is CR062
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns Customer has invalid decision code