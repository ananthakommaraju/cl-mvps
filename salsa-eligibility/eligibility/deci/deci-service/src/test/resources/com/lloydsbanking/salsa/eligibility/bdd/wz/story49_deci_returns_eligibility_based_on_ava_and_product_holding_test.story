Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for AVA's and product holdings based on instruction mnemonic
to determine eligibility

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for customers
with AVAs less than maximum AVAs

Given customer has AVAs less than maximum AVAs and rule is CR017
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for customers
with AVAs more than maximum AVAs

Given customer has AVAs more than maximum AVAs and rule is CR017
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns description for CR017

Scenario: 3. Determine eligible customer instruction service evaluates eligibility true for customers
with product holdings less than maximum product holdings

Given customer has product holdings less than maximum product holdings and rule is CR020
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 4. Determine eligible customer instruction service evaluates eligibility false for customers
with product holdings more than maximum product holdings

Given customer has product holdings more than maximum product holdings and rule is CR020
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns description for CR020

Scenario: 5. Determine eligible customer instruction service evaluates eligibility true for customer
having no existing product holdings

Given customer does not have any product holding and rule is CR017
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true
