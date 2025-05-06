Meta:

Narrative:
Determine eligible customer instruction service should check shadow limit
to determine eligibility for requested products

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for customer having current
account and shadow limit amount not 0

Given shadow limit amount is non zero for the account
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility true for customer having current
account and shadow limit amount 0 and accountType not C

Given shadow limit amount is zero for the account
And AccountType is not C
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 3. Determine eligible customer instruction service evaluates eligibility true for customer having current
account and shadow limit amount 0 and accountType C and unmatched sellerLegalEntity

Given shadow limit amount is zero for the account
And AccountType is C and sellerLegalEntity not matched
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 4. Determine eligible customer instruction service evaluates eligibility false for customer having current
account and shadow limit amount 0 and accountType C and matched sellerLegalEntity

Given shadow limit amount is zero for the account
And AccountType is C and sellerLegalEntity matched
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns decline reasons in response for failing CR022

Scenario: 5. Determine eligible customer instruction service evaluates eligibility true for customer having current
account and shadow limit amount greater than threshold

Given shadow limit amount is greater than assigned for the account
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true


Scenario: 6. Determine eligible customer instruction service evaluates eligibility true for customer having current
account and shadow limit amount less than threshold and accountType not C

Given shadow limit amount less than threshold for the account
And AccountType is not C
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 7. Determine eligible customer instruction service evaluates eligibility true for customer having current
account and shadow limit amount less than threshold and accountType C and unmatched sellerLegalEntity

Given shadow limit amount less than threshold for the account
And AccountType is C and sellerLegalEntity not matched
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 8. Determine eligible customer instruction service evaluates eligibility false for customer having current
account and shadow limit amount  less than threshold and accountType C and matched sellerLegalEntity

Given shadow limit amount less than threshold for the account
And AccountType is C and sellerLegalEntity matched
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns decline reasons in response for failing CR023

