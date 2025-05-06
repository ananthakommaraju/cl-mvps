Meta:

Narrative:
Activate Product Arrangement creates a new card in FDI

Scenario: 1. APACC creates a card in FDI
Given product features are retrieved
And F241V1 responds successfully
When there is a call to APACC
Then APACC creates the card successfully and service continues

Scenario: 2. APACC does not create a card in FDI
Given product features are retrieved
And F241V1 responds with error
When there is a call to APACC
Then APACC service continues

Scenario: 3. APACC creates a card in FDI
Given sub status is card creation failure (1013)
And F241V1 responds successfully
When there is a call to APACC
Then APACC creates the card successfully and service continues
