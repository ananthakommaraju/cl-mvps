Meta:

Narrative:
Process Pending Arrangement service sets Activate Product Arrangement Flag for awaiting referral application status

Scenario: 1. PPAE sets Activate Product Arrangement Flag To True for awaiting referral application status
Given application status is Awaiting Referral in request
When There is a call to PPAE
Then PPAE sets Activate Product Arrangement Flag To True