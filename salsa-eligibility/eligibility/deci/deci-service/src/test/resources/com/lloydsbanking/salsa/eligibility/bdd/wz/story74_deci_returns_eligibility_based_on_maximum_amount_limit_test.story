Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for ISA based on maximum amount limit.

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true when existing arrangements does not exist

Given existing arrangement does not exist in the request
And rule is CR069
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility true when parent instruction mnemonic is ISA
 and maximum limit amount of ISA balance is greater than 0

Given parent instruction mnemonic is ISA and maximum limit amount of ISA balance is greater than 0
And rule is CR069
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true and returns decline reason as Funds deposited within the same tax year.

Scenario: 3. Determine eligible customer instruction service evaluates eligibility true when parent instruction mnemonic does not have ISA

Given parent instruction mnemonic does not have ISA
And rule is CR069
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 4. Determine eligible customer instruction service evaluates eligibility true when parent instruction mnemonic has ISA and maximum amount limit of ISA balance is less than 0

Given parent instruction mnemonic hasISA and maximum limit amount of ISA balance is less than 0
And rule is CR069
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 5. Determine eligible customer instruction service evaluates eligibility true when candidate instruction is non ISA

Given candidateInstruction is non ISA
And rule is CR069
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true



