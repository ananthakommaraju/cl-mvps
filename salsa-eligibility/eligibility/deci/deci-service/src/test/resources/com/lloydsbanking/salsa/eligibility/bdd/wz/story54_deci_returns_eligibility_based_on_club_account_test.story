Meta:

Narrative:
Determine eligible customer instruction service should check eligibility based
on club account to determine eligibility.

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for customers
having club account

Given customer has club account and rule is CR049
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility true for customers
does not having club account

Given customer doesnot have club account and rule is CR049
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns description Customer does not hold Club Account