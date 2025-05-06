Meta:

Narrative:
As the unauth current account journey I want to retrieve the eligible offered products

Scenario: 1 OPAPCA does not return any eligible product

Given application status is approved/refer
And offered product list contains associated product of priority 0
When UI calls OPAPCA with valid request
Then OPAPCA returns applied product in response



Scenario: 4 OPAPCA does not return eligible product

Given application status is approved/refer
And offered product list contains associated product and other products with higher priority than associated product and switch value is zero
When UI calls OPAPCA with valid request
Then OPAPCA does not return any eligible product