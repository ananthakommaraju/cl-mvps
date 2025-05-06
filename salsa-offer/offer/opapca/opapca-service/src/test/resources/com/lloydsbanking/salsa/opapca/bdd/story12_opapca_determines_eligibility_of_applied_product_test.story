Narrative:
OPAPCA determines eligibility of the applied product

Scenario: 1 OPAPCA returns eligibility details

Given Customer is not guardian
And BFPO Indicator is not present and eligibility is true
When UI calls OPAPCA with valid request
Then OPAPCA returns eligibility details and existing product arrangements in response

Scenario: 2 OPAPCA returns returns eligibility details

Given Customer is not guardian
And BFPO Indicator is present and eligibility is true
When UI calls OPAPCA with valid request
Then OPAPCA returns eligibility details and existing product arrangements in response

Scenario: 3 OPAPCA returns eligibility details

Given Customer is not guardian
And BFPO Indicator is not present and eligibility is false
When UI calls OPAPCA with valid request
Then OPAPCA returns response

Scenario: 4 OPAPCA returns returns eligibility details

Given Customer is not guardian
And BFPO Indicator is present and eligibility is false
When UI calls OPAPCA with valid request
Then OPAPCA returns response

Scenario: 5 OPAPCA returns returns eligibility details

Given Customer is guardian
When UI calls OPAPCA with valid request
Then OPAPCA returns eligibility details as null