Meta:

Narrative:
Activate Product Arrangement service should responds to requests


Scenario: 1. APASA responds to valid request
Given that sort code is present in request
When The UI calls APASA with valid request
Then APASA returns valid response