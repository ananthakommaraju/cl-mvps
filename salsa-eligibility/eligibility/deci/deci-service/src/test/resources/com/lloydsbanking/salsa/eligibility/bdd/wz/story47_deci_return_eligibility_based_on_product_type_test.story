Meta:

Narrative:
Determine eligible customer instruction service should check existing MonthlySaver product
to determine if the customer can apply for new MonthlySaver product

Scenario: 1. DECI service returns eligibility as true for applied MonthlySaver when customer does not have any other existing MonthlySaver product

Given Customer has applied for MonthlySaver product
And he does not have any existing MonthlySaver product
When the UI calls DECI with valid request
Then DECI evaluates eligibility as true

Scenario: 2.  DECI service returns eligibility as false for applied MonthlySaver product when customer has any other existing MonthlySaver product

Given Customer has applied for MonthlySaver product
And he has an existing MonthlySaver product
When the UI calls DECI with valid request
Then DECI evaluates eligibility as false and returns decline reason for applied product
