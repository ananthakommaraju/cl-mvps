Meta:

Narrative:
Activate Product Arrangement creates standing order based on deposit arrangement

Scenario: 1. APAPCA creates standing order
Given interest remittance details were captured
And Create standing order responds without exception
When there is a call to APAD
Then APAPCA responds successfully

Scenario: 2. APAPCA does not create standing order due to exception
Given interest remittance details were captured
And Create standing order responds with exception
When there is a call to APAD
Then APAPCA service continues
