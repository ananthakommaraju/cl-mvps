Meta:

Narrative:
Activate Product Arrangement adds OMS Offers in OCIS

Scenario: 1. APACC adds OMS Offers in OCIS
Given product is added successfully
And F251 responds successfully
When there is a call to APACC
Then APACC adds the OMS Offers

Scenario: 2. APACC does not add OMS Offers in OCIS
Given product is added successfully
And F251 responds with error
When there is a call to APACC
Then APACC does not add the OMS offers and assigns Application SubStatus

Scenario: 3. APACC does not add OMS Offers in OCIS
!--Given sub status is add oms offers (1015)
!--When there is a call to APACC
!--Then APACC adds the OMS Offers