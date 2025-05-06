Meta:

Narrative:
Process Pending Arrangement service calls SendCommunication Service for CCA Signed/Pending application status based on sendEmailDate

Scenario: 1. PPAE calls SendCommunication Service for CCA Signed application status for sendemaildate present in request
Given application status is CCA Signed and application type is Loan and difference in current date and PAM last updated date is within configured threshold
When There is a call to PPAE
Then PPAE calls SendCommunication Service successfully

Scenario: 2. PPAE calls SendCommunication Service for CCA Pending application status for sendemaildate present in request
Given application status is CCA Pending and application type is Loan and difference in current date and PAM last updated date is within configured threshold
When There is a call to PPAE
Then PPAE calls SendCommunication Service successfully

Scenario: 3. PPAE does not call SendCommunication Service for CCA Pending application status for emaildate not greater than zero present in request
Given application status is CCA Pending and application type is Loan and difference in current date and PAM last updated date is zero
When There is a call to PPAE
Then PPAE calls SendCommunication Service successfully





