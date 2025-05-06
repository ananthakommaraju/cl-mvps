Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for credit card accounts based on parent instruction mnemonic
to determine eligibility

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for customers with credit card accounts

Given customer is applying for balance transfer and have eligible credit card account/s
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service throws error for customers with no credit card account

Given customer is applying for balance transfer and does not have any eligible credit card account
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false for balance transfer and returns error condition for ineligible credit card accounts
