Meta:

Narrative:
As the Unauth Sales journey I want to call OCIS API F060 to update marketing preference so that I can complete activation of the valid application

Scenario: 1. APACC calls F060 to update marketing preference when application sub status is Marketing Preference Update Failure(1029)
!--Given Sub status is Marketing Preference Update Failure(1029)
!--And F060 call is successful
!--When UI calls APAD
!--Then update application status as null and service continues

Scenario: 2. APACC update application status to Awaiting Fulfilment(1009), sub-status to Marketing Preference Update Failure(1029) and increment Retry count in PAM when application sub status is Marketing Preference Update Failure(1029) and F060 call fails
!--Given Sub status is Marketing Preference Update Failure(1029)
!--And F060 call fails
!--When UI calls APAD
!--Then update application status to 1009, sub-status to Marketing Preference Update Failure(1029) and increment Retry count in PAM

Scenario: 3. APACC calls F060 to update marketing preference when application sub status is null and previous calls are successful
Given Sub status is null
When UI calls APAD
Then call OCIS API F060 to update marketing preference.