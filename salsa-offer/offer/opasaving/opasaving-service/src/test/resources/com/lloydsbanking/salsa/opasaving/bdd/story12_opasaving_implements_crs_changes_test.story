Meta:

Narrative:
OfferProductArrangementSaving implements cRS changes.

Scenario: 1 OpaSaving create crs customer record in PAM.
Given nationality is present
When UI calls OpaSaving with valid request
Then OpaSaving saves party contry associations with value and type in PAM

