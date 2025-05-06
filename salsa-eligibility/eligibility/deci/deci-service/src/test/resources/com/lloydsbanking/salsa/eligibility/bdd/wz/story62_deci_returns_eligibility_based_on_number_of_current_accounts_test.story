Meta:

Narrative:
Determine eligible customer instruction service should check eligibility based on number of current
accounts a customer hold.

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for
customers having current accounts less than threshold

Given customer has current accounts less than threshold
And rule is CR042
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true


Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for
customers having current accounts more than threshold

Given customer has current accounts more than threshold
And rule is CR042
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns Customer cannot have more than threshold instances of the product.

Scenario: 3. Determine eligible customer instruction service evaluates eligibility true when
customer does not hold any product

Given customer does not hold any product
And rule is CR042
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true
