Meta:

Narrative:
As the loans journey I want OPALOANS to retrieve product arrangement details from PAM and return affiliate details in response.

Scenario: 1 OPALOANS retrieves product arrangement details from PAM DB and updates Gender as 000 when Gender is undefined
Given application exists in PAM DB
And Gender is undefined
When UI calls OPALOANS with valid request
Then OPALOANS returns affiliate details in response
And OPALOANS updates Gender as 000 in response