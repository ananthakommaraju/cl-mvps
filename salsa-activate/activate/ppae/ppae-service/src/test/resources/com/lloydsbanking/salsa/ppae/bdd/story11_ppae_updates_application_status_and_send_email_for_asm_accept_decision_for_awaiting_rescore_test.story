Meta:

Narrative:
Process Pending Arrangement Service Calls Send Communication Service Based On ASM Decisions For Application Status Awaiting Rescore


Scenario: 1. PPAE sets retry count zero for Arrangement Type CC And Application Status Awaiting Rescore
Given Asm decision is Accept and Arrangement Type is CC
And ProdOfferIdentifier is matched
When There is a call to PPAE
Then PPAE calls activate and modify product service


Scenario: 2. PPAE sets retry count zero for Arrangement Type SA And Application Status Awaiting Rescore
Given Asm decision is Accept and Arrangement Type is SA
When There is a call to PPAE
Then PPAE calls activate product service


Scenario: 3. PPAE Update Details In Pam For Asm Accept, Arrangement Type CC And Application Status Awaiting Rescore
Given Asm decision is Accept and Arrangement Type is CC for ProdIdentifier
And ProdOfferIdentifier is not matched
When There is a call to PPAE
Then PPAE calls modify product and  send communication

