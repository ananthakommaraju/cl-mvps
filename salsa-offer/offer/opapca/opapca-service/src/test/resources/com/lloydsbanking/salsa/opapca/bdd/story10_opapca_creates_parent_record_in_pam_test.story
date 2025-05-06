Meta:

Narrative:
OfferProductArrangementPCA should create a customer record in PAM.

Scenario: 1 OPAPCA create parent record in PAM.
Given arrangement Id exist in PAM
When UI calls OPAPCA with valid request
Then OPAPCA return appId and application status in response.