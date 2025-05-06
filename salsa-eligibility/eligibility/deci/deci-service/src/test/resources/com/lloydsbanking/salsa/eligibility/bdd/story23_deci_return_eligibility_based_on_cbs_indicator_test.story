Meta:

Narrative:
Determine eligible customer instruction service should check cbs indicator
to determine eligibility for RBBS overdraft and loan products

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for
 RBBS overdraft when cbs indicator 646 is not set on the applied account

Given CBS 646 indicator is not set on a account for which overdraft is being applied
And rule is CR056
When the UI calls DECI for RBBS overdraft
Then DECI evaluates eligibility to true


Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for
 RBBS overdraft when cbs indicator 646 is set on the applied account

Given CBS 646 indicator is  set on a account for which overdraft is being applied
And rule is CR056
When the UI calls DECI for RBBS overdraft
Then DECI evaluates eligibility to false


Scenario: 3. Determine eligible customer instruction service evaluates eligibility true for
 RBBS loans when cbs indicator 646 is not set on any of the accounts

Given CBS 646 indicator is not  set on any account
And rule is CR059
When the UI calls DECI for RBBS loans
Then DECI evaluates eligibility to true

Scenario: 4. Determine eligible customer instruction service evaluates eligibility false for
 RBBS loans when cbs indicator 646 is  set on any of the accounts

Given CBS 646 indicator is set on one account
And rule is CR059
When the UI calls DECI for RBBS loans
Then DECI evaluates eligibility to false

Scenario: 5. Determine eligible customer instruction service evaluates eligibility false for
 RBBS loans when cbs indicator 8 is  set on any of the accounts

Given CBS 8 indicator is set on one account for RBBS loan
And rule is CR061
When the UI calls DECI for RBBS loans
Then DECI evaluates eligibility to false

Scenario: 6. Determine eligible customer instruction service evaluates eligibility true for
 RBBS loans when cbs indicator 8 is not set on any of the accounts

Given CBS 8 indicator is not set on any account for RBBS loan
And rule is CR061
When the UI calls DECI for RBBS loans
Then DECI evaluates eligibility to true

Scenario: 7. Determine eligible customer instruction service evaluates eligibility false for
 RBBS Overdraft when cbs indicator 8 is  set on any of the accounts

Given CBS 8 indicator is set on one account for RBBS Overdraft
And rule is CR061
When the UI calls DECI for RBBS overdraft
Then DECI evaluates eligibility to false

Scenario: 8. Determine eligible customer instruction service evaluates eligibility true for
 RBBS Overdraft when cbs indicator 8 is not set on any of the accounts

Given CBS 8 indicator is not set on any account for RBBS Overdraft
And rule is CR061
When the UI calls DECI for RBBS overdraft
Then DECI evaluates eligibility to true

