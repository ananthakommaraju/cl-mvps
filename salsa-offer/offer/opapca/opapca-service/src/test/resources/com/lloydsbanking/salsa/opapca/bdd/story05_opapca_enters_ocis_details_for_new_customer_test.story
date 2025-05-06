Meta:

Narrative:
As the unauth current account journey I want to enter details of a customer in OCIS

Scenario: 1 OPACA enters details in OCIS

Given New Customer Indicator is true for a customer
And there is no error from ocis f062
When UI calls OPAPCA with valid request
Then OPAPCA enters details in OCIS for the customer



