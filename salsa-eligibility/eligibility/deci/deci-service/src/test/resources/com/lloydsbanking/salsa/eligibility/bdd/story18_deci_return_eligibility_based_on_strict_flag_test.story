Meta:

Narrative:
Determine eligible customer instruction service should check strict flag
to determine eligibility for RBBS overdraft

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for
RBBS overdraft when strict flag value for the account is not equal to set threshold

Given strict flag value for the account is not equal to set threshold for which overdraft is being applied
When the UI calls DECI for RBBS overdraft
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility true for
RBBS overdraft when strict flag value for the account is equal to set threshold

Given strict flag value for the account is equal to set threshold for which overdraft is being applied
When the UI calls DECI for RBBS overdraft
Then DECI evaluates eligibility to false for RBBS overdraft and returns error condition