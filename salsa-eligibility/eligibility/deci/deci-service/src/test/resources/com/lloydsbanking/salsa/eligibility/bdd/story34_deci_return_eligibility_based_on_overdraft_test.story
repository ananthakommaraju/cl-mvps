Meta:

Narrative:
Determine eligible customer instruction service should check overdraft against threshold
to determine eligibility

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for  customer not having overdraft applied in the last threshold days on the account

Given customer is applying for amending business overdraft that has not been applied  in last threshold days
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility false for customer having overdraft applied in the last threshold days on the account
Given customer is applying for amending business overdraft  that has been applied  in last threshold days.
When the UI calls DECI with valid request
Then DECI evaluates eligibility to false and  returns error condition for overdraft applied in the last threshold days on the account.


Scenario: 3. Determine eligible customer instruction service evaluates eligibility true for customer not having overdraft expiring in  less than threshold days on the account.

Given customer is applying for amending business overdraft that is not expiring in less than threshold days.
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true


Scenario: 4. Determine eligible customer instruction service evaluates eligibility true for customer having overdraft expiring in less than threshold days on the account.

Given customer is applying for amending business overdraft that is expiring in less than threshold days.
When the UI calls DECI with valid request
Then DECI evaluates eligibility to  false and  returns error condition for overdraft expiring in less than threshold days on the account.

Scenario: 5. Determine eligible customer instruction service evaluates eligibility true for customer not having overdraft already expired

Given customer is applying for amending business overdraft that has not expired.
When the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 6. Determine eligible customer instruction service evaluates eligibility false for customer  having overdraft already expired

Given customer is applying for amending business overdraft that has expired.
When the UI calls DECI with valid request
Then DECI evaluates eligibility to  false and  returns error condition for overdraft already expired.