Meta:

Narrative:
As the Unauth Sales journey I want to APAD to send communication - welcome mail for successful account creation so that I can complete activation of the valid application

Scenario: 1. APAD send communication welcome mail to customer for successful account creation if application sub status is null and arrangement type is saving (SA)
Given application sub status is null
And arrangement type is saving
When UI calls APAD
Then send welcome mail to customer with SA template

Scenario: 2. APAD send communication with attachment to customer if durable medium switch is on and external template id is present and arrangement type is not saving
!-- Given application sub status is null
!-- And arrangement type is not saving
!-- And durable medium switch is on
!-- And external template id is present
!-- When UI calls APAD
!-- Then send communication with attachment to customer

Scenario: 3. APAD send communication welcome mail to customer for successful account creation if durable medium switch is off and arrangement type is not saving
!-- Given application sub status is null
!-- And arrangement type is not saving
!-- And durable medium switch is off
!-- When UI calls APAD
!-- Then send welcome mail to customer with CA template

Scenario: 4. APAD send communication welcome mail to customer for successful account creation if template id is not present and arrangement type is not saving
!-- Given application sub status is null
!-- And arrangement type is not saving
!-- And external template id is not present
!-- When UI calls APAD
!-- Then send welcome mail to customer with CA template
