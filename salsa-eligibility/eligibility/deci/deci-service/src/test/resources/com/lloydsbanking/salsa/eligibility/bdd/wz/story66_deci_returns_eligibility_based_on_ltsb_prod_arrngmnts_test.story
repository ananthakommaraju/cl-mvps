Meta:

Narrative:
Determine eligible customer instruction service should validate Address and Individual name to determine eligibility for requested products


Scenario: 1. Determine eligible customer instruction service evaluates eligibility false when
customer does not hold any ltsb product

Given customer doesnot hold any ltsb product
And rule is CR033
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns Customer does not have LTSB account

Scenario: 2. Determine eligible customer instruction service evaluates eligibility true when
customer hold any ltsb product

Given customer does hold any ltsb product
And rule is CR033
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true





