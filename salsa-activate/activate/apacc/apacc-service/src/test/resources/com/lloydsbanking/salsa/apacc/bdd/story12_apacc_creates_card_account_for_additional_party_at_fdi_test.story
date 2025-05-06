Meta:

Narrative:
Activate Product Arrangement creates a card in FDI for additional card holder

Scenario: 1. APACC creates a card in FDI for additional card holder
Given product features are retrieved and has additional party
And F241 responds successfully
When there is a call to APACC
Then APACC creates the card successfully and service continues
