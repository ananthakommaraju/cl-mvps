Meta:

Narrative:
Process Pending Arrangement service processes awaiting post fulfilment application

Scenario: 1. PPAE service communicates failed balance transfer status when application verification fails
Given application status is AWAITING_POST_FULFILMENT_PROCESS (1013)
And balance transfer is for greater than threshold amount
And retrieve product conditions succeeds with btOffAttribute
And application verification fails
When there is call to PPAE
Then service communicates balance transfer status to customer

Scenario: 2. PPAE service terminates when retrieve product conditions fails
Given application status is AWAITING_POST_FULFILMENT_PROCESS (1013)
And retrieve product conditions fails
When there is call to PPAE
Then service terminates

Scenario: 3. PPAE service communicates failed balance transfer status to customer when status is not checked for balance transfer greater than threshold amount
Given application status is AWAITING_POST_FULFILMENT_PROCESS (1013)
And balance transfer is for greater than threshold amount
And retrieve product conditions succeeds with btOffAttribute
And application verification succeeds
And balance transfer status from issue in payment is not checked or failed for balance transfer
When there is call to PPAE
Then service communicates balance transfer status to customer

Scenario: 4. PPAE service communicates partially balance transfer status to customer when status is not success for all balance transfer greater than threshold amount
Given application status is AWAITING_POST_FULFILMENT_PROCESS (1013)
And balance transfer is for greater than threshold amount
And retrieve product conditions succeeds with btOffAttribute
And application verification succeeds
And balance transfer status from issue in payment is not success for all balance transfer
When there is call to PPAE
Then service communicates balance transfer status to customer

Scenario: 5. PPAE service communicates successful balance transfer status to customer when status is success for all balance transfer greater than threshold amount
Given application status is AWAITING_POST_FULFILMENT_PROCESS (1013)
And balance transfer is for greater than threshold amount
And retrieve product conditions succeeds with btOffAttribute
And application verification succeeds
And balance transfer status from issue in payment is success for all balance transfer
When there is call to PPAE
Then service communicates balance transfer status to customer

Scenario: 6. PPAE service communicates failed balance transfer status to customer when status is not checked for balance transfer less than threshold amount
Given application status is AWAITING_POST_FULFILMENT_PROCESS (1013)
And balance transfer is for less than threshold amount
And retrieve product conditions succeeds with btOffAttribute
And balance transfer status from issue in payment is not checked or failed for balance transfer
When there is call to PPAE
Then service communicates balance transfer status to customer

Scenario: 7. PPAE service communicates partially balance transfer status to customer when status is not success for all balance transfer less than threshold amount
Given application status is AWAITING_POST_FULFILMENT_PROCESS (1013)
And balance transfer is for less than threshold amount
And retrieve product conditions succeeds with btOffAttribute
And balance transfer status from issue in payment is not success for all balance transfer
When there is call to PPAE
Then service communicates balance transfer status to customer

Scenario: 8. PPAE service communicates successful balance transfer status to customer when status is success for all balance transfer less than threshold amount
Given application status is AWAITING_POST_FULFILMENT_PROCESS (1013)
And balance transfer is for less than threshold amount
And retrieve product conditions succeeds with btOffAttribute
And balance transfer status from issue in payment is success for all balance transfer
When there is call to PPAE
Then service communicates balance transfer status to customer
