Meta:

Narrative:
As a Unauth Sales Journey I want APASA to update customer and guardian id for old customer

Scenario: 1. APASA continues when guardian details are not present
Given application sub status is null and a case of OAP
When there is call to APASA for old customer
Then service continues



