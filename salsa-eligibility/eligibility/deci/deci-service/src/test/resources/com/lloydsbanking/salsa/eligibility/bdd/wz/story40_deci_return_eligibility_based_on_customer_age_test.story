Meta:

Narrative:
Determine eligible customer instruction service should check customer age
to determine eligibility for requested products

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for customer age
younger than threshold

Given customer is not older than 74 years of age and channel is IBL
And he is applying for personal loans
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for customer age older than threshold

Given customer is older than 74 years of age and channel is IBL
And he is applying for personal loans
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns error condition for age older than threshold


Scenario: 3. Determine eligible customer instruction service evaluates eligibility true for customer age
older than threshold

Given customer is not younger than 16 years of age and channel is IBL
And he is applying for cash isa
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 4. Determine eligible customer instruction service evaluates eligibility false for customer age
younger than threshold

Given customer is younger than 16 years of age and channel is IBL
And he is applying for cash isa
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false for cash isa and returns error condition for age younger than threshold

Scenario: 5. Determine eligible customer instruction service evaluates eligibility true for customer age older than threshold

Given customer is not younger than 18 years of age and channel is LTB
And he is applying for P_LOAN_STP
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true


Scenario: 6. Determine eligible customer instruction service evaluates eligibility false for customer age younger than threshold

Given customer is  younger than 18 years of age and channel is LTB
And he is applying for P_LOAN_STP
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false for P_LOAN_STP and returns error condition for customer age younger than threshold
