Narrative:
OPASaving determines eligibility of the applied product

Scenario: 1 OPASaving returns existing product arrangements and no eligibility details in response

Given Customer is not guardian
And BFPO Indicator is not present and eligibility is true
When UI calls OPASaving with valid request
Then OPASaving returns existing product arrangements in response
And eligibility details are not returned in response

Scenario: 2 OPASaving returns no existing product arrangements and no eligibility details in response

Given Customer is not guardian
And BFPO Indicator is present and eligibility is true
When UI calls OPASaving with valid request
Then OPASaving returns no existing product arrangements in response
And eligibility details are not returned in response

Scenario: 3 OPASaving returns no existing product arrangements and eligibility details in response

Given Customer is not guardian
And BFPO Indicator is not present and eligibility is false
When UI calls OPASaving with valid request
Then OPASaving returns no existing product arrangements in response
And eligibility details are returned in response

Scenario: 4 OPASaving returns no existing product arrangements and eligibility details in response

Given Customer is not guardian
And BFPO Indicator is present and eligibility is false
When UI calls OPASaving with valid request
Then OPASaving returns no existing product arrangements in response
And eligibility details are returned in response

Scenario: 5 OPASaving returns no existing product arrangements and no eligibility details in response

Given Customer is guardian
When UI calls OPASaving with valid request
Then OPASaving returns no existing product arrangements in response
And eligibility details are not returned in response
