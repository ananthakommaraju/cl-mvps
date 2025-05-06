Meta:

Narrative:
As the unauth current account journey I want OPAPCA to update the application details in PAM and return the affiliate details.

Scenario: 1 OPAPCA updates application details in PAM and returns affiliate details

Given applicant type is not guardian
When UI calls OPAPCA with valid request
Then OPAPCA updates application details in PAM and returns affiliate details