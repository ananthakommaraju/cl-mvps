Meta:

Narrative:
Determine eligible customer instruction service should check eligibility for ppc on account


Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for unavailable embeddedInsurance
Given embeddedInsurance is unavailable
When the UI calls DECI with valid request
Then DECI evaluates eligibility as true


Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for available embeddedInsurance
Given embeddedInsurance is available
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false  and returns  errror condition  for  available embeddedInsurance
