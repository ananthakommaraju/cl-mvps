Meta:

Narrative:
Determine eligible customer instruction service should check shadow limit
to determine eligibility for requested products

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for customer having current
account and shadow limit amount greater than threshold

Given shadow limit amount is greater than threshold and rule is CR023
And customer has current account
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true


Scenario: 2. Determine eligible customer instruction service evaluates eligibility true for customer having current
account and shadow limit amount less than 0

Given shadow limit amount less than 0 and rule is CR023
And customer has current account
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 3. Determine eligible customer instruction service evaluates eligibility true for customer not having current
account and shadow limit amount is between 0 and threshold

Given shadow limit amount  is between 0 and threshold and rule is CR023
And customer does not have current account
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 4. Determine eligible customer instruction service evaluates eligibility false for customer having current
account and shadow limit amount is between 0 and threshold

Given shadow limit amount  is between 0 and threshold and rule is CR023
And customer has current account
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns decline reasons in response for failing CR023

