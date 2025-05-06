Meta:

Narrative:
As the current account journey I want to OPAPCA to respond to valid request for different F336 and F204 Response

Scenario: 1 OPAPCA responds to valid request when responds with Empty AmdEffDt for a product

Given F336 responds with Empty AmdEffDt for a product
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and asm score as accept for the customer

Scenario: 2 OPAPCA responds to valid request with no AddressLinePaf list in postal address

Given F204 responds with no AddressLinePaf list in postal address
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and asm score as accept for the customer

