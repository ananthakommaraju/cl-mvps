Meta:

Narrative:
As Galaxy I want SALSA to rescore CC applications that are in a Awaiting Rescore status


Scenario: 1. PPAE Service Calls Credit Rating Service For Application Status Awaiting Rescore
Given credit decision is refer and productType is CC
And referral code is  501 for f424
When UI calls PPAE with valid request
Then PPAE invokes modify product arrangement


Scenario: 2. PPAE Service Calls Credit Rating Service For Application Status Awaiting Rescore
Given credit decision is refer and productType is CC
And referral code is not 501 for F424
When UI calls PPAE with valid request
Then PPAE does not change status


Scenario: 3. PPAE Service Calls Credit Rating Service For Application Status Awaiting Rescore
Given credit decision is refer and productType is SA
And referral code is  501 for 204
When UI calls PPAE with valid request
Then PPAE invokes modify product arrangement

Scenario: 4. PPAE Service Calls Credit Rating Service For Application Status Awaiting Rescore
Given credit decision is refer and productType is SA
And referral code is not 501 for F204
When UI calls PPAE with valid request
Then PPAE does not change status

Scenario: 5.PPAE Service Calls Credit Rating Service For CC and F424 throws error
Given credit decision is refer and productType is CC
And f424 throws error
When UI calls PPAE with valid request
Then PPAE logs the error

Scenario: 6. PPAE Service Calls Credit Rating Service For SA and F204 throws error
Given credit decision is refer and productType is SA
And f204 throws error
When UI calls PPAE with valid request
Then PPAE logs error

Scenario: 7.PPAE Service Calls Credit Rating Service For Application Status Awaiting Rescore
Given credit decision is refer and productType is CA
And referral code is not 501 for F205 and 204
When UI calls PPAE with valid request
Then PPAE invokes modify product arrangement

Scenario: 8.PPAE Service Calls Credit Rating Service For Application Status Awaiting Rescore
Given credit decision is refer and productType is CA
And f205 throws error
When UI calls PPAE with valid request
Then PPAE invokes modify product arrangement











