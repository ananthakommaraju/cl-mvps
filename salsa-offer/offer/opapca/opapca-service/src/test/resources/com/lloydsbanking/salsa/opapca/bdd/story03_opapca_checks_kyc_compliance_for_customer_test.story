Meta:

Narrative:
As the unauth current account journey I want to check if the customer is KYC compliant

Scenario: 1 OPAPCA returns EIDV status accept for KYC compliant customer

Given Customer is identified at OCIS
And has product holdings
And evidence data is available at OCIS
When UI calls OPAPCA with valid request
Then OPAPCA returns EIDV status as acccept for the customer

Scenario: 2 OPAPCA returnsEIDV status N/A for non KYC compliant child customer

When UI calls OPAPCA with valid request for child customer
Then OPAPCA returns EIDV status as N/A for the customer

Scenario: 3 OPAPCA returns EIDV status accept for KYC compliant customer

Given Customer is identified at OCIS
And has product holdings
And evidence data is not available at OCIS
When UI calls OPAPCA with valid request
Then OPAPCA returns EIDV status as refer for the customer
