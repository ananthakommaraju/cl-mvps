Meta:

Narrative:
Determine eligible customer instruction service should check eligibility based on joint signatory accounts.

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for
customers having some accounts as joint signatory accounts

Given customer has some accounts as joint signatory account
And rule is CR068
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true


Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for
customers having all accounts as joint signatory accounts

Given customer has all accounts as joint signatory account
And rule is CR068
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns Customer has all Joint Signatory Accounts


