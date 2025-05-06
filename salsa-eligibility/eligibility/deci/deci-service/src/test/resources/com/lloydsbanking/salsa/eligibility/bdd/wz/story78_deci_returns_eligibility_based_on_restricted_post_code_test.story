Meta:

Narrative:
Determine eligible customer instruction service should check eligibility based on restricted post code
and foreign address indicator

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for
customer who does not belong to restricted post code

Given Customer does not belong to restricted post code
And rule is CR066
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for
customer who belongs to restricted post code

Given Customer belongs to restricted post code
And rule is CR066
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns Customer fails UK residency check

Scenario: 3. Determine eligible customer instruction service evaluates eligibility false for
customer having foreign address indicator as true

Given Customer has foreign address indicator true
And rule is CR066
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns Customer fails UK residency check
