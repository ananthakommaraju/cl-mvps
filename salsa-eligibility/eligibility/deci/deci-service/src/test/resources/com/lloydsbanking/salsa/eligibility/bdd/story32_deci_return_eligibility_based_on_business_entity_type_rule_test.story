Meta:

Narrative:
Determine eligible customer instruction service should check business entity type
to determine eligibility for RBBS Customers for business loan and Overdraft

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for
 RBBS Overdraft when business entity type is valid

Given valid business entity type is set on business for which OD is being applied
And rule is CR051
When the UI calls DECI
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for
RBBS Overdraft when business entity type is invalid

Given invalid business entity type is set on business for which OD is being applied
And rule is CR051
When the UI calls DECI
Then DECI evaluates eligibility to false

Scenario: 3. Determine eligible customer instruction service evaluates eligibility true for
 RBBS Loans when business entity type is valid

Given valid business entity type is set on business for which loan is being applied
And rule is CR051
When the UI calls DECI
Then DECI evaluates eligibility to true

Scenario: 4. Determine eligible customer instruction service evaluates eligibility false for
RBBS Loans when business entity type is invalid

Given invalid business entity type is set on business for which loan is being applied
And rule is CR051
When the UI calls DECI
Then DECI evaluates eligibility to false

