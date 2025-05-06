Meta:

Narrative:
Determine eligible customer instruction service should check shadow limit and related events against threshold
to determine eligibility

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for customer having current
shadow limit less than set threshold and related event present

Given shadow limit is greater than set threshold
And related events are present
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true


Scenario: 2. Determine eligible customer instruction service evaluates eligibility true for customer having current
shadow limit less than set threshold and related event not present

Given shadow limit is less than set threshold
And related events are not present
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns error condition


Scenario: 3. Determine eligible customer instruction service evaluates eligibility true for customer having current
shadow limit greater than set threshold and related event not present

Given shadow limit is greater than set threshold
And related events are not present
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns error condition


Scenario: 4. Determine eligible customer instruction service evaluates eligibility false for customer having current
shadow limit greater than set threshold and related event present

Given shadow limit is less than set threshold
And related events are present
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns error condition