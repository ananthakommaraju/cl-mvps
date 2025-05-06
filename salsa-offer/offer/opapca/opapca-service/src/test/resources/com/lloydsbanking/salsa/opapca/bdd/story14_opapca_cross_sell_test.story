Meta:

Narrative:
OfferProductArrangementPCA responds to cross sell request

Scenario: 1 OPAPCA returns application status as 1002 to cross sell request

Given related application id present in request
And related application status is fulfilled
When UI calls OPAPCA with valid request
Then OPAPCA returns application status as 1002


Scenario: 2 OPAPCA returns application status as 1002 to cross sell request

Given related application id present in request
And related application status is not fulfilled
When UI calls OPAPCA with valid request
Then OPAPCA returns application status as 1014


Scenario: 3 OPAPCA returns application status same as cross sell request

Given related application id present in request
And related application status is not present in request
When UI calls OPAPCA with valid request
Then OPAPCA returns application status as null