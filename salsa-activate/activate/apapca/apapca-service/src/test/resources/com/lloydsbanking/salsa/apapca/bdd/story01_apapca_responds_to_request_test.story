Meta:

Narrative:
Activate Product Arrangement service should responds to requests


Scenario: 1. APAPCA responds to valid request
Given that sort code is present in request
When The UI calls APAPCA with valid request
Then APAPCA returns valid response