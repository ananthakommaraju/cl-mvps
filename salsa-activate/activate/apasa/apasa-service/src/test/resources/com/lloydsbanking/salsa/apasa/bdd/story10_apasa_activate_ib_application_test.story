Meta:

Narrative:
As a Unauth Sales Journey I want to check asm decision so that complete the activation of valid application

Scenario: 1. APASA calls B751 to activate IB application when application sub status is null
Given application sub status is Null
When UI calls APAD
Then call B751 BAPI to activate IB application

Scenario: 2. APASA calls B751 to activate IB application when application sub status is 1028
Given Application Sub status is 1028
When UI calls APAD
Then call B751 BAPI to activate IB application with subStatus 1028
