Meta:

Narrative:
Determine eligible customer instruction service should check shadow limit
to determine eligibility for requested products

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for customer having shadow limit amount not 0

Given shadow limit amount is nonZero for the account
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility true for customer not having current
account and shadow limit amount 0

Given shadow limit amount is zero for the account
And productType is not 1 and systemCode is not 00004
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 3. Determine eligible customer instruction service evaluates eligibility false for customer having current
account and shadow limit amount 0

Given shadow limit amount is zero for the account
And productType is 1 and systemCode is 00004
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false
And Reason description for rule CR022








