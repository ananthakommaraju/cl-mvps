Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for current account based
on product holdings.

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true when existing arrangements does not exist

Given existing arrangement does not exist in the request
And rule is CR040
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for customer having parentInstructionMnemonic as G_VANTAGE and have more than maxNumOfVantage instances of the product.

Given customer have more than maximum number of vantage instances of the product
And rule is CR040
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns Customer cannot have more than maxNumOfVantage instances of the product.

Scenario: 3. Determine eligible customer instruction service evaluates eligibility true for for customer having parentInstructionMnemonic as G_VANTAGE and  do not have more than maxNumOfVantage instances of the product.

Given customer do not have more than maximum number of vantage instances of the product
And rule is CR040
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true




