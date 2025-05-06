Meta:

Narrative:
As the credit card journey I want to check if the customer is KYC compliant

Scenario: 1 OPACC returns EIDV status accept for KYC compliant customer

Given Customer is identified at OCIS
And has product holdings
And evidence data is available at OCIS
When UI calls OPACC with valid request
Then OPACC returns EIDV status as accept for the customer


Scenario: 2 OPACC returns EIDV status decline for KYC non compliant customer

Given Customer is identified at OCIS
And has product holdings
And evidence data is not available at OCIS
When UI calls OPACC with valid request
Then OPACC returns EIDV status as decline for the customer