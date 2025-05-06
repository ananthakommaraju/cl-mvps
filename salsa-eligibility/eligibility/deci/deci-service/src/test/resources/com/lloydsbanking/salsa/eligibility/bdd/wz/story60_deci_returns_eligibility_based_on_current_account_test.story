Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for personal loan based
on current account.

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for customer
having productType equal to 1 and system code as 0004

Given customer has current account and rule is CR036
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for customer
does not have productType equal to 1 and system code as 0004

Given customer does not have current account and rule is CR036
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns Customer doesn't have a current account
