Meta:

Narrative:
As the credit card journey I want to enter details of a customer in OCIS

Scenario: 1 OPACC enters details in OCIS

Given New Customer Indicator is true for a customer
And there is no error from ocis f062
When UI calls OPACC with valid request
Then OPACC enters details in OCIS for the customer



