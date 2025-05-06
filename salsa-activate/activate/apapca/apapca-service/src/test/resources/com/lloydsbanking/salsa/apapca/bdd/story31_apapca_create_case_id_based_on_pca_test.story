Meta:

Narrative:
As the Unauth Sales journey I want to call PEGA API to Create Case Service Service to create case id of pca

Scenario: 1. APAPCA calls PEGA API to Create Case Service Service to create case id of pca
Given INTEND_TO_SWITCH condition is present for the application
And success response received from PEGA API
When UI calls APAD
Then call PEGA API Create Case Service Service to create case id of pca

Scenario: 2. APAPCA assign conditions when call to PEGA API fails
Given INTEND_TO_SWITCH condition is present for the application
And call to PEGA API fails
When UI calls APAD
Then service assign conditions