Meta:

Narrative:
OfferProductArrangementSaving should create a customer record in PAM.

Scenario: 1 OpaSaving create custoemr record in PAM.
When UI calls OpaSaving with valid request
Then OpaSaving return PAM appId in response.