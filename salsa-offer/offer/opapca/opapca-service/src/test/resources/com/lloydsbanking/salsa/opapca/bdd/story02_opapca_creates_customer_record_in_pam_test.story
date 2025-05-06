Meta:

Narrative:
OfferProductArrangementPCA should create a customer record in PAM.

Scenario: 1 OPAPCA create custoemr record in PAM.
When UI calls OPAPCA with valid request
Then OPAPCA return PAM appId in response.