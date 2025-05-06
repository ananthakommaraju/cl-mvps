Meta:

Narrative:
Determine eligible customer instruction service should check eligibility based
on cbs accounts with indicators that customer hold

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for customer do not have cbs accounts with indicators present

Given customer do not have cbs accounts with indicators
And rule is CR041
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for customer have cbs accounts with indicators present

Given customer have cbs accounts with indicators
And rule is CR041
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns customer have CBS accounts with indicators



