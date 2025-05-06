Meta:

Narrative:
As the Unauth Sales journey I want to APAD to send communication - welcome mail for successful account creation so that I can complete activation of the valid application

Scenario: 1. APAD send communication welcome mail to customer for successful account creation if application sub status is null and arrangement type is saving (SA)
Given application sub status is null
When UI calls APAD
Then send welcome mail to customer with SA template

