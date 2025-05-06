Meta:

Narrative:
As the Saving Account journey I want OpaSaving to update the application details in PAM and return the affiliate details.

Scenario: 1 OpaSaving updates application details in PAM and returns affiliate details

Given applicant type is not guardian
When UI calls OpaSaving with valid request
Then OpaSaving updates application details in PAM and returns affiliate details