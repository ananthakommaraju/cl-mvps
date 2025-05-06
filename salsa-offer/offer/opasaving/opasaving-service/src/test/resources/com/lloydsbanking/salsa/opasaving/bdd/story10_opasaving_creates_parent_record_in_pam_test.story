Meta:

Narrative:
OfferProductArrangementSaving should create a customer record in PAM.

Scenario: 1 OpaSaving create parent record in PAM.
Given arrangement Id exist in PAM
When UI calls OpaSaving with valid request
Then OpaSaving return appId and application status in response.