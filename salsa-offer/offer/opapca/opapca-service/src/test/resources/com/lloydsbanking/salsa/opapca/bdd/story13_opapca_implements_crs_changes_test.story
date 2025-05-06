Meta:

Narrative:
OfferProductArrangementPCA implements cRS changes.

Scenario: 1 OPAPCA create crs customer record in PAM.
Given nationality is present
When UI calls OPAPCA with valid request
Then OPAPCA saves party contry associations with value and type in PAM

