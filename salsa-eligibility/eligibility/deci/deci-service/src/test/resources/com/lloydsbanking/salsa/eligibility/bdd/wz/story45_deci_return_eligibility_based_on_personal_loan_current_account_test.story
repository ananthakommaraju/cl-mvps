Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for personal loan Current account customer holds

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for personal loan Current account is available and rule is CR024

Given Current account is available for the customer and rule is CR024
When the UI calls DECI for product
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility true for personal loan Current account is not available and rule is CR024

Given Current account is not available for the customer and rule is CR024
When the UI calls DECI for product
Then DECI evaluates eligibility to false and return error condition Customer doesn’t have current account of logged in Channel and  has a no loan
