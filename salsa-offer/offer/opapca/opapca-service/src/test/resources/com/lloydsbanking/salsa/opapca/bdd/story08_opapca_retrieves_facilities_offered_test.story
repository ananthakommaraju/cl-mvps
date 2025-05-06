Meta:

Narrative:
As the unauth current account journey I want to retrieve the facilities offers

Scenario: 1 OPAPCA returns conditions for check book and debit card

Given application status is approved/refer and rpc is called
And Credit Score returns option code for check book
When UI calls OPAPCA with valid request
Then OPAPCA returns conditions for check book and debit card

Scenario: 2 OPAPCA returns conditions for overdraft and debit card

Given application status is approved/refer and rpc is called
And Credit Score returns option code for overdraft
When UI calls OPAPCA with valid request
Then OPAPCA returns conditions for overdraft and debit card

Scenario: 3 OPAPCA returns conditions for credit card and debit card

Given application status is approved/refer and rpc is called
And Credit Score returns option code for credit card
When UI calls OPAPCA with valid request
Then OPAPCA returns conditions for credit card and debit card

Scenario: 4 OPAPCA returns conditions for debit card

Given application status is approved/refer and rpc is called
And Credit Score do not return option code for credit card, check book and overdraft
When UI calls OPAPCA with valid request
Then OPAPCA returns conditions for debit card

Scenario: 5 OPAPCA returns conditions for check book,overdraft and debit card

Given application status is approved/refer and rpc is called
And Credit Score returns option code for check book and overdraft
When UI calls OPAPCA with valid request
Then OPAPCA returns conditions for check book, overdraft and debit card

Scenario: 6 OPAPCA returns conditions for check book,credit card and debit card

Given application status is approved/refer and rpc is called
And Credit Score returns option code for check book and credit card
When UI calls OPAPCA with valid request
Then OPAPCA returns conditions for check book, credit card and debit card

Scenario: 7 OPAPCA returns conditions for check book,credit card and debit card

Given application status is approved/refer and rpc is called
And Credit Score returns option code for credit card and overdraft
When UI calls OPAPCA with valid request
Then OPAPCA returns conditions for credit card, overdraft and debit card

Scenario: 8 OPAPCA returns conditions for check book, overdraft, credit card and debit card

Given application status is approved/refer and rpc is called
And Credit Score returns option code for check book, overdraft and credit card
When UI calls OPAPCA with valid request
Then OPAPCA returns conditions for check book, overdraft, credit card and debit card





