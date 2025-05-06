Meta:

Narrative:
Activate Product Arrangement service retrieve product features based on product identifier

Scenario: 1. APAPCA retrieve product features based on product identifier
Given Fulfill bank account arrangement is called and there is a internal call to RetrieveProductConditions with product Id as input
When there is a call to APAD
Then APAPCA responds successfully
