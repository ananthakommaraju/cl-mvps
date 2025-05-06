Meta:

Narrative:
As the unauth current account journey I want to return a decision using the ASM score on the application
and retrieve the product offers (upsell/downsell)

Scenario: 1 OPAPCA returns ASM status as accept

Given Fraud check is negative and status is accept
And Credit Score is accept
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and asm score as accept for the customer

Scenario: 2 OPAPCA returns ASM status as refer

Given Fraud check is negative and status is refer
And Credit Score is accept
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and asm score as refer for the customer

Scenario: 3 OPAPCA returns ASM status as unscored

Given Fraud check is negative and status is accept
And Credit Score is refer and referral code is 501
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and asm score as unscored

Scenario: 4 OPAPCA returns ASM status as refer

Given Fraud check is negative and status is accept
And Credit Score is refer and referral code is 601
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and asm score as refer

Scenario: 5 OPAPCA returns ASM status as decline

Given Fraud check is negative and status is accept
And Credit Score is decline
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and asm score as decline for the customer

Scenario: 6 OPAPCA returns ASM status as decline

Given Fraud check is negative and status is refer
And Credit Score is decline
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and asm score as decline for the customer

Scenario: 7 OPAPCA returns ASM status as unscored

Given Fraud check is negative and status is refer
And Credit Score is refer and referral code is 501
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and asm score as unscored for the customer

Scenario: 8 OPAPCA returns ASM status as refer

Given Fraud check is negative and status is refer
And Credit Score is refer and referral code is 601
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and asm score as refer for the customer


Scenario: 9 OPAPCA returns ASM status as refer

Given Fraud check is negative and status is refer
And Credit Score is accept
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and asm score as refer for the customer

Scenario: 10 OPAPCA returns ASM status as unscored

Given Fraud check is negative and status is refer and referral code is 501
When UI calls OPAPCA with valid request
Then OPAPCA returns application status as unscored for the customer

Scenario: 11 OPAPCA returns ASM status as decline

Given Fraud check is negative and status is decline
When UI calls OPAPCA with valid request
Then OPAPCA returns application status as decline for the customer

