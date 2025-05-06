Meta:

Narrative:
Process Pending Arrangement service calls Send Communication Service for ASM Decline And Application Status Rescore

Scenario: 1. PPAE sends email for ASM Decline, Arrangement Type CC And Application Status Awaiting Rescore
Given Asm decision is Decline,Arrangement Type  CC and decline source BUREAU
When There is a call to PPAE
Then PPAE calls send communications service

Scenario: 2. PPAE sends email for ASM Decline, Arrangement Type CC And Application Status Awaiting Rescore
Given Asm decision is Decline,Arrangement Type  CC and decline source duplicate application
When There is a call to PPAE
Then PPAE calls send communications service

Scenario: 3. PPAE sends email for ASM Decline, Arrangement Type CA And Application Status Awaiting Rescore
Given Asm decision is Decline,Arrangement Type  CA and decline source BUREAU
When There is a call to PPAE
Then PPAE calls send communications service




