Meta:

Narrative:
Determine eligible customer instruction service should check eligibility based on current account.

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for
customers having current account

Given customer has current account
And rule is CR034
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true


Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for
customers who does not have current account

Given customer does not have current account
And rule is CR034
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns Customer does not have a current account

