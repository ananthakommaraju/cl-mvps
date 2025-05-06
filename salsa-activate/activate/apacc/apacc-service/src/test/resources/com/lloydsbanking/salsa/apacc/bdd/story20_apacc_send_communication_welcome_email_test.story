Meta:

Narrative:
Activate Product Arrangement send communication welcome email to customer

Scenario: 1. APACC send communication welcome email with template WELCOME_EMAIL
Given previous calls are successful
And send communication welcome email was successful
When there is a call to APACC
Then send communication with template WELCOME_EMAIL





