Meta:

Narrative:
Process Pending Arrangement service processes awaiting referral LRA application for fulfilment

Scenario: 1. PPAE service continues when retrieve loan details succeeds for awaiting referral LRA application (1012)
Given application status is REFERRAL_PROCESSED and TMS task id is present in pam
And retrieve loan details succeeds
When there is call to PPAE for LRA application
Then service continues

Scenario: 2. PPAE service continues when retrieve loan details fails for awaiting referral LRA application (1012)
Given application status is REFERRAL_PROCESSED and TMS task id is present in pam
And retrieve loan details fails
When there is call to PPAE for LRA application
Then service continues

Scenario: 3. PPAE service continues when prepare finance service arrangement succeeds for awaiting referral LRA application (1012)
Given application status is REFERRAL_PROCESSED and TMS task id is present in pam
And asm decision is decline or accept
And prepare finance service arrangement succeeds
When there is call to PPAE for LRA application
Then service continues

Scenario: 4. PPAE service continues when prepare finance service arrangement fails for awaiting referral LRA application (1012)
!--Given application status is REFERRAL_PROCESSED and TMS task id is present in pam
!--And asm decision is decline or accept
!--And prepare finance service arrangement fails
!--When there is a call to PPAE for LRA application
!--Then service continues

Scenario: 5. PPAE service continues when retrieve personal details succeeds for awaiting referral LRA application (1012)
Given application status is REFERRAL_PROCESSED and TMS task id is present in pam
And asm decision is decline or accept
And retrieve personal details succeeds
When there is call to PPAE for LRA application
Then service continues

Scenario: 6. PPAE service continues when retrieve personal details fails for awaiting referral LRA application (1012)
Given application status is REFERRAL_PROCESSED and TMS task id is present in pam
And asm decision is decline or accept
And retrieve personal details fails
When there is call to PPAE for LRA application
Then service continues

Scenario: 7. PPAE service communicates email to customer with template LRA_ACCEPT_MSG when personal details (email) is present and asm decision is accept
Given application status is REFERRAL_PROCESSED and TMS task id is present in pam
And asm decision is accept
And email is present
When there is call to PPAE for LRA application
Then service communicates email to customer with template LRA_ACCEPT_MSG


Scenario: 8. PPAE service retrieves look up value for decline template with success when personal details (email) is present and asm decision is decline
Given application status is REFERRAL_PROCESSED and TMS task id is present in pam
And asm decision is decline
And email is present
And retrieve look up value for decline template succeeds
When there is call to PPAE for LRA application
Then service communicates email to customer with decline template

Scenario: 9. PPAE service communicates email to customer with default decline template LRA_DECLINE_MSG when retrieve decline template fails
Given application status is REFERRAL_PROCESSED and TMS task id is present in pam
And asm decision is decline
And email is present
And retrieve look up value for decline template fails
When there is call to PPAE for LRA application
Then service communicates email to customer with default decline template LRA_DECLINE_MSG