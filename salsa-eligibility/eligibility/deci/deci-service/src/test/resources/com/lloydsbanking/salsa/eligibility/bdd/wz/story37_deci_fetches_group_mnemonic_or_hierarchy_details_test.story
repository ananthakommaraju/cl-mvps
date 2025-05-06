Narrative:
As a user interface
I want determine eligible customer instruction service fetches group mnemonic or hierarchy details.

Scenario: 1.DECI service fetches group mnemonic or grand parent hierarchy details.

Given candidateInstruction is G_ISA
When DECI invokes retrieveInstructionHierarchyForGrandParent with a valid request
Then DECI responds

Scenario: 2.DECI service fetches group mnemonic or child instruction hierarchy details.
Given candidateInstruction is not G_ISA
When DECI invokes retrieveChildInstructionHierarchy with a valid request
Then DECI responds
