Meta:

Narrative:
Determine eligible customer instruction service should check MBC role
to determine eligibility

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for customer doesnot have MBC role

Given customer doesnot has MBC role
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for customer have MBC role

Given customer has MBC role
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns error condition for having MBC role.

