Meta:

Narrative:
Determine eligible customer instruction service should check kyc status
to determine eligibility

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for full kyc status

Given KYC status is Full for the customer
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for partial kyc status

Given KYC status is partial for the customer
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns error condition for partial KYC status

Scenario: 3. Determine eligible customer instruction service throws error for unavailable kyc status

Given KYC status is not available for the customer
When the UI calls DECI with valid request
Then DECI  returns error condition for unavailable KYC status