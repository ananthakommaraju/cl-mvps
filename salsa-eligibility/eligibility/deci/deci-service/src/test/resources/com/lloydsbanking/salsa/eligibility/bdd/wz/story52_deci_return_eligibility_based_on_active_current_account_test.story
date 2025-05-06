Meta:

Narrative:
Determine eligible customer instruction service should check active current account
to determine eligibility for personal loan

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for personal loan which have Active Current account
Given Status Code is 001 and status is Effective and the rule is CR038
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for  for personal loan which does not have Active Current account

Given Status Code is 001 and status is Dormant and the rule is CR038
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns Customer doesn't have an active current account

Scenario: 3. Determine eligible customer instruction service evaluates eligibility false for  for personal loan which does not have Active Current account

Given Status Code is 002 and status is Effective and the rule is CR038
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and returns Customer doesn't have an active current account
