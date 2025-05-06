Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for car finance based on fulfilled finance application

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for
customer has no fulfilled finance application

Given customer has no fulfilled finance application
And rule is CR063
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for
customer already has fulfilled finance application

Given customer has fulfilled finance application
And rule is CR063
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns Customer already has fulfilled finance application

