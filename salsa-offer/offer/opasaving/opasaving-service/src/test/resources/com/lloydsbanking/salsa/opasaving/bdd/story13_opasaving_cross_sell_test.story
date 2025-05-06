Meta:

Narrative:
OfferProductArrangementSaving responds to cross sell request

Scenario: 1 OpaSaving returns application status as 1002 to cross sell request

Given related application id present in request
And related application status is fulfilled
When UI calls OpaSaving with valid request
Then OpaSaving returns application status as 1002


Scenario: 2 OpaSaving returns application status as 1002 to cross sell request

Given related application id present in request
And related application status is not fulfilled
When UI calls OpaSaving with valid request
Then OpaSaving returns application status as 1014


Scenario: 3 OpaSaving returns application status same as cross sell request

Given related application id present in request
And related application status is not present in request
When UI calls OpaSaving with valid request
Then OpaSaving returns application status same as request