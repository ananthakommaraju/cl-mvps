Narrative:
Determine eligible customer instruction service should check Account opening date of Loan Current account and Credit card
to determine eligibility for requested Business products Amend Overdraft Apply Overdraft and Apply Loan

Scenario: 1. Determine eligible customer instruction service evaluates eligibility true for Business Loan when account
opening date for credit card is greater than threshold

Given he is applying for business loan and rule is CR053
When customer has not opened credit card in last 2 days
And the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 2. Determine eligible customer instruction service evaluates eligibility true for Business Loan when account
 opening date for loan account is greater than threshold

Given he is applying for business loan and rule is CR054
When customer has not opened loan account in last 28 days
And the UI calls DECI with valid request
Then DECI evaluates eligibility to true


Scenario: 3. Determine eligible customer instruction service evaluates eligibility true for Business Loan and Overdraft
on current account is not opened prior to threshold

Given he is applying for business loan and rule is CR055
When customer has not opened Overdraft on current account in last 28 days
And the UI calls DECI with valid request
Then DECI evaluates eligibility to true


Scenario: 4. Determine eligible customer instruction service evaluates eligibility true for Business Overdraft when
account opening date for credit card is greater than threshold

Given he is applying for business Overdraft and rule is CR053
When customer has not opened credit card in last 2 days
And the UI calls DECI with valid request
Then DECI evaluates eligibility to true



Scenario: 5. Determine eligible customer instruction service evaluates eligibility true for Business Overdraft when
 account opening date for loan account is greater than threshold

Given he is applying for business Overdraft and rule is CR054
When customer has not opened loan account in last 28 days
And the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 6. Determine eligible customer instruction service evaluates eligibility true for Business Overdraft and
 Overdraft on current account is not opened prior to threshold

Given he is applying for business Overdraft and rule is CR055
When customer has not opened Overdraft on current account in last 28 days
And the UI calls DECI with valid request
Then DECI evaluates eligibility to true

Scenario: 7. Determine eligible customer instruction service evaluates eligibility false for Business Loan when account
opening date for credit card is less than threshold

Given he is applying for business loan and rule is CR053
When customer has opened credit card in last 2 days
And the UI calls DECI with valid request
Then DECI evaluates eligibility to false

Scenario: 8. Determine eligible customer instruction service evaluates eligibility false for Business Loan when account
 opening date for loan account is less than threshold

Given he is applying for business loan and rule is CR054
When customer has opened loan account in last 28 days
And the UI calls DECI with valid request
Then DECI evaluates eligibility to false


Scenario: 9. Determine eligible customer instruction service evaluates eligibility false for Business Loan and Overdraft
on current account is opened prior to threshold

Given he is applying for business loan and rule is CR055
When customer has opened Overdraft on current account in the last 28 days
And the UI calls DECI with valid request
Then DECI evaluates eligibility to false


Scenario: 10. Determine eligible customer instruction service evaluates eligibility false for Business Overdraft when
account opening date for credit card is less than threshold

Given he is applying for business Overdraft and rule is CR053
When customer has opened credit card in last 2 days
And the UI calls DECI with valid request
Then DECI evaluates eligibility to false



Scenario: 11. Determine eligible customer instruction service evaluates eligibility false for Business Overdraft when
 account opening date for loan account is less than threshold

Given he is applying for business Overdraft and rule is CR054
When customer has opened loan account in last 28 days
And the UI calls DECI with valid request
Then DECI evaluates eligibility to false

Scenario: 12. Determine eligible customer instruction service evaluates eligibility true for Business Overdraft and
 Overdraft on current account is opened prior to threshold

Given he is applying for business Overdraft and rule is CR055
When customer has opened Overdraft on current account in the last 28 days
And the UI calls DECI with valid request
Then DECI evaluates eligibility to false