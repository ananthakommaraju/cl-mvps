Meta:

Narrative:
As a Unauth Sales Journey I want APAPCA to update customer and guardian id for old customer

Scenario: 1. APAPCA retrieves evidence and purpose lookUp data for old customer when sub status is update customer record failure (1018)
Given application sub status is update customer record failure
And evidence and purpose lookUp data call succeeds
When there is call to APAPCA for old customer
Then service continues

Scenario: 2. APAPCA continues for old customer when evidence and purpose lookUp data fails
Given application sub status is update customer record failure
And evidence and purpose lookUp data call fails
When there is call to APAPCA for old customer
Then service continues

Scenario: 3. APAPCA updates customer id when call to F062 succeeds for customer details
Given application sub status is update customer record failure
And call to F062 succeeds for customer details
When there is call to APAPCA for old customer
Then update customer id and sub status as null

Scenario: 4. APAPCA updates application sub status as Update Customer Record Failure (1018) when F062 fails for customer details
Given application sub status is update customer record failure
And call to F062 fails for customer details
When there is call to APAPCA for old customer
Then update application sub status as update customer record failure

Scenario: 5. APAPCA updates guardian id when call to F062 succeeds for guardian details
Given application sub status is update customer record failure
And guardian details are present
And call to F062 succeeds for guardian details
When there is call to APAPCA for old customer
Then update guardian id and sub status as null

Scenario: 6. APAPCA updates application sub status as Update Customer Record Failure (1018) when F062 fails for guardian details
Given application sub status is update customer record failure
And guardian details are present
And call to F062 fails for guardian details
When there is call to APAPCA for old customer
Then update application sub status as update customer record failure

Scenario: 7. APAPCA continues when guardian details are not present
Given application sub status is update customer record failure
And guardian details are not present
When there is call to APAPCA for old customer
Then service continues



