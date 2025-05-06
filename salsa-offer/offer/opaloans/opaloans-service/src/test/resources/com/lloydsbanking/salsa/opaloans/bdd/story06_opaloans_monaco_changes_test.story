Meta:

Narrative:
As the loans journey I want OPALOANS to retrieve product arrangement details from PAM and return affiliate details in response.

Scenario: 1 OPALOANS retrieves product arrangement details from PAM DB and updates Gender as 000 when Gender is undefined
Given application type is 1005
And loan mode is 1
When UI calls OPALOANS with valid request
Then OPALOANS returns affiliate details in response


