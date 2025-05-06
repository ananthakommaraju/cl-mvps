Meta:

Narrative:
As the saving account journey I want to enter details of a customer in OCIS

Scenario: 1 OPASaving enters details in OCIS

Given New Customer Indicator is true for a customer
And there is no error from ocis f062
When UI calls OPASaving with valid request
Then OPASaving enters details in OCIS for the customer

Scenario: 2 OPASaving throws error when non zero error code returned from OCIS

Given New Customer Indicator is true for a customer
And there is non zero error code from ocis f062
When UI calls OPASaving with valid request
Then OPASaving throws exception to the calling component