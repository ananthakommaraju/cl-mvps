Meta:

Narrative:
Determine eligible customer instruction service should check existing Cash ISA product
to determine if the customer can apply for new Cash ISA product

Scenario: 1. DECI service returns eligibility as true for applied cash ISA product when customer does not have any other existing cash ISA product
Given Customer has applied for cash ISA product
And he does not have any existing cash ISA product
When the UI calls DECI with valid request
Then deci evaluates eligibility as true


Scenario: 2.  DECI service returns eligibility as false for applied cash ISA product when customer has any other existing cash ISA product

Given Customer has applied for cash ISA product
And he has an existing cash ISA product
When the UI calls DECI with valid request
Then deci evaluates eligibility as false and returns decline reason for applied cash ISA product
