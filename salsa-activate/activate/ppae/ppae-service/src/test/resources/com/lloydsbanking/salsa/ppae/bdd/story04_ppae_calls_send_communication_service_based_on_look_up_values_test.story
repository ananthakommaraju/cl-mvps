Meta:

Narrative:
Process Pending Arrangement service calls sendCommunication and schedule communication service service to send email and sms based on lookupdata from PAM

Scenario: 1. PPAE calls send communication service to send email to the customer based on lookup values from PAM
Given lookup data from PAM is available and the difference between the no. of configured days
And the no. of days after update is less than five in request
When There is a call to PPAE
Then PPAE calls sendCommunication service successfully

