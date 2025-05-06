Meta:

Narrative:
As a Unauth Sales Journey I want to check eidv status and set fulfil eligibility flag so that complete the activation of valid application

Scenario: 1. APAPCA set fulfilment eligibility flag as true when the application status is approved (1002) and related application id is not present in the request
Given updated Application status is approved
And related application id is present in request
When there is call to APAPCA
Then set fulfilment eligibility flag as true and service continues

Scenario: 2. APAPCA set fulfilment eligibility flag as true when the application status is approved (1002) and related application id is present and eidv status is accept
!-- Given updated Application status is approved
!-- And related application id is not present in request
!-- And eidv status is accept
!-- When there is call to APAPCA
!-- Then set fulfilment eligibility flag as true and service continues

Scenario: 3. APAPCA sends communication to customer and set application status as manual idv (1007) when the application status is approved (1002) and related application id is present and eidv status is refer
!-- Given updated Application status is approved
!-- And related application id is not present in request
!-- And eidv status is refer
!-- When there is call to APAPCA
!-- Then call send communication and set application status as manual idv
