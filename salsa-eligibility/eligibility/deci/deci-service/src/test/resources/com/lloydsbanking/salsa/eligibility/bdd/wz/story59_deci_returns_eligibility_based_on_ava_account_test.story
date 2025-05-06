Meta:

Narrative:
Determine eligible customer instruction service should check eligibility based on AVA account.

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for
customers having AVA account

Given customer has AVA account
And rule is CR032
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true


Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for
customers who does not have AVA account

Given customer doesnot have AVA account
And rule is CR032
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns Customer does not have AVA Account

Scenario: 3. Determine eligible customer instruction service evaluates eligibility false when
customer does not hold any product

Given customer does not hold any product
And rule is CR032
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns Customer does not have any Account
