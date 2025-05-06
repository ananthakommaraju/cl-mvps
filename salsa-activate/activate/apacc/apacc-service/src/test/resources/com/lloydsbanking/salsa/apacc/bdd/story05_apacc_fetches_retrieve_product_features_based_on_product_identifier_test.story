Meta:

Narrative:
Activate Product Arrangement service retrieve product features based on product identifier

Scenario: 1. APACC retrieve product features based on product identifier
Given Fulfill credit card arrangement is called
Given there is a internal call to RetrieveProductConditions with product Id as input
When there is a call to APACC
Then APACC responds successfully
