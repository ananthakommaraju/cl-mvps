Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for personal loan based
on dormant account.
Scenario: 1. Determine eligible customer instruction service evaluates eligibility false for customer
having indicator code is same as threshold

Given customer has dormant account and rule is CR044
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns Customer has a Dormant Account

Scenario: 2. Determine eligible customer instruction service evaluates eligibility true for customer
does not have dormant account

Given customer does not have dormant account and rule is CR044
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario:3. Determine eligible customer instruction service evaluates eligibility true for customer
does not have any product
Given customer does not have any product and rule is CR044
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario:4. Determine eligible customer instruction service evaluates eligibility true for customer
does not have indicator list
Given customer does not have indicator list and rule is CR044
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true