Meta:

Narrative:
As the saving account journey I want to check if the customer is KYC compliant

Scenario: 1 OpaSaving returns EIDV status accept for KYC compliant customer

Given Customer is identified at OCIS
And has product holdings
And evidence data is available at OCIS
When UI calls OpaSaving with valid request
Then OpaSaving returns EIDV status as accept for the customer

Scenario: 2 OpaSaving returnsEIDV status N/A for non KYC compliant child customer

When UI calls OpaSaving with valid request for child customer
Then OpaSaving returns EIDV status as N/A for the customer

Scenario: 3 OpaSaving returns EIDV status refer for KYC non compliant customer

Given Customer is identified at OCIS
And has product holdings
And evidence data is not available at OCIS
When UI calls OpaSaving with valid request
Then OpaSaving returns EIDV status as refer for the customer


