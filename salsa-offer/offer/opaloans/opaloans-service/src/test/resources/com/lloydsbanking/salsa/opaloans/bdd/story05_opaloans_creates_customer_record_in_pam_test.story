Meta:

Narrative:
As the loans journey I want OPALOANS to create customer record in PAM and return application ID in response.

Scenario: 1 OPALOANS creates customer record in PAM when related application id and BFPO address indicator are not present and customer is existing
Given related application id and BFPO address indicator are not present
And affiliate details are present
And customer is existing
When UI calls OPALOANS with valid request
Then OPALOANS creates customer record in PAM
And OPALOANS returns arrangement id and affiliate details in response

Scenario: 2 OPALOANS does not create customer record in PAM when related application id and BFPO address indicator are not present and customer is not existing
Given related application id and BFPO address indicator are not present
And affiliate details are present
And customer is not existing with additional data indicator true
When UI calls OPALOANS with valid request
Then OPALOANS does not create customer record in PAM and does not return arrangement id and affiliate details in response

Scenario: 3 OPALOANS does not create customer record in PAM when related application id and BFPO address indicator are not present and customer is not existing
Given related application id and BFPO address indicator are not present
And affiliate details are present
And customer is not existing with additional data indicator false
When UI calls OPALOANS with valid request
Then OPALOANS does not create customer record in PAM and does not return arrangement id and affiliate details in response