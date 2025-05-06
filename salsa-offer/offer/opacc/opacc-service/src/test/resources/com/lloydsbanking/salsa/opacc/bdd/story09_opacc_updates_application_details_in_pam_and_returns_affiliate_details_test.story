Meta:

Narrative:
As the credit card journey I want OPACC to update the application details in PAM and return the affiliate details.

Scenario: 1 OPACC updates application details in PAM and returns affiliate details

Given applicant type is not guardian
When UI calls OPACC with valid request
Then OPACC updates application details in PAM and returns affiliate details