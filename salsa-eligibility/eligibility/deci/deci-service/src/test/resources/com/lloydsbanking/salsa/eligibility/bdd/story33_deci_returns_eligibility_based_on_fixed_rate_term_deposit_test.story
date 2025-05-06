Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for fixed rate term deposit("G_ONL_FRTD") based
on instruction mnemonic and parent instruction mnemonic to determine eligibility

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for customers with  not more than 5 product holdings of fixed rate term deposit

Given customer doesn't have more than 5 product holdings of fixed rate term deposit
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service throws error for customers with more than 5 product holdings of fixed rate term deposit

Given customer have more than 5 product holdings of fixed rate term deposit
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns error condition for customers having more than 5 product holdings
