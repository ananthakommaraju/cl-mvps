Meta:

Narrative:
Activate Product Arrangement creates a new card in FDI

Scenario: 1. APACC activates IB application
Given generate doc is called successfully
And B751 responds successfully
When there is a call to APACC
Then APACC activates ib application and service continues

Scenario: 2. APACC does not activate IB application
Given generate doc is called successfully
And B751 responds with error
When there is a call to APACC
Then APACC service continues
