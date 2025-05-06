Meta:

Narrative:
OfferProductArrangementSaving returns Preferential Rate Ientifier of applied product

Scenario: 1 OpaSaving return preferentialRateIentifier in response
Given Customer is existing
And SCSP Switch is enabled
When UI calls OpaSaving with valid request
Then OpaSaving return preferentialRateIdentifier in response

Scenario: 2 OpaSaving returns valid response
Given Customer is existing
And SCSP Switch is not enabled
When UI calls OpaSaving with valid request
Then OpaSaving returns valid response

Scenario: 3 OpaSaving returns valid response
Given Customer is new customer
When UI calls OpaSaving with valid request
Then OpaSaving returns valid response














