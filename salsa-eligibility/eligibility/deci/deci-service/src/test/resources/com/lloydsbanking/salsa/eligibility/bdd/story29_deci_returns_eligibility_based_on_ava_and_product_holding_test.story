Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for AVA's and product holdings based on instruction mnemonic
to determine eligibility

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for customers with  not more than 10 AVA's

Given customer doesn't have more than 10 AVA's and rule is CR017
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service returns error for customers with more than 10 AVA's

Given customer  have more than 10 AVA's and rule is CR017
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns error condition for customers having more than 10 AVA's

Scenario: 3. Determine eligible customer instruction service evaluates eligibility true for customers with  not more than 5 product holdings

Given customer doesn't have more than 5 product holdings and rule is CR020
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 4. Determine eligible customer instruction service throws error for customers with more than 5 product holdings

Given customer have more than 5 product holdings and rule is CR020
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns error condition for customers having more than 5 product holdings