Meta:

Narrative:
Activate Product Arrangement service calls B675 to create account at CBS end

Scenario: 1. APASA calls E502 amend roll over account
Given Fulfill pending saving account  is called
And E502 amend roll over account is called
When APAD is called with Application sub status is null and is secondary flag is true and account number is present in response
Then APASA returns response
