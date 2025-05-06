Meta:

Narrative:
Determine eligible customer instruction service should execute the rules in priority Order set for the RBBS Products

Scenario: 1. Determine eligible customer instruction service should execute the rules in priority Order set for the RBBS Products

Given Audit Events rule set prior to default rules
And all rules are passed
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service should execute the rules in priority Order set for the RBBS Products and returns the default rule
decline message if it has decline in default rule

Given Audit Events rule set prior to default rules
And default rule has error
When the UI calls DECI with default rule condition fail
Then DECI evaluates eligibility to false with default rule decline condition

