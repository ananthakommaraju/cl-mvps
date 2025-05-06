Meta:

Narrative:
Process Pending Arrangement service calls RetrieveLoanDetails Service for CCA Signed/Pending application status

Scenario: 1. PPAE calls RetrieveLoanDetails Service for CCA Signed/Pending application status in request
Given application status is CCA Signed/Pending and application type is Loan and zero loanAgreementNumber
When There is a call to PPAE
Then PPAE calls  RetrieveLoanDetails Service successfully