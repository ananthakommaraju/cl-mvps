Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for personal loan based
on cbs event

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for customer
not having indicator code same as threshold

Given customer does not return post from the address
And rule is CR046
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for customer
having indicator code same as threshold

Given customer returns post from the address
And rule is CR046
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns Post returned from this address

