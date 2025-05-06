Meta:

Narrative:
As a Unauth Sales Journey I want APACC to update customer and guardian id for old customer

Scenario: 1. APACC updates customer id when call to F062 succeeds for customer details
!--Given application sub status is update customer record failure
!--And call to F062 succeeds for customer details
!--When there is call to APACC for old customer
!--Then update customer id and sub status as null

Scenario: 2. APACC updates application sub status as Update Customer Record Failure (1018) when F062 fails for customer details
!--Given application sub status is update customer record failure
!--And call to F062 fails for customer details
!--When there is call to APACC for old customer
!--Then update application sub status as update customer record failure

Scenario: 3. APACC updates guardian id when call to F062 succeeds for guardian details
Given application sub status is update customer record failure
And guardian details are present
And call to F062 succeeds for guardian details
When there is call to APACC for old customer
Then update guardian id and sub status as null

Scenario: 4. APACC updates application sub status as Update Customer Record Failure (1018) when F062 fails for guardian details
Given application sub status is update customer record failure
And guardian details are present
And call to F062 fails for guardian details
When there is call to APACC for old customer
Then update application sub status as update customer record failure

Scenario: 5. APACC continues when guardian details are not present
!--Given application sub status is update customer record failure
!--And guardian details are not present
!--When there is call to APACC for old customer
!--Then service continues



