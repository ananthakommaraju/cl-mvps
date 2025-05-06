Meta:

Narrative:
As the saving account journey I want to return a decision using the ASM score on the application
and retrieve the product offers (upsell/downsell)

Scenario: 1 OpaSaving returns ASM status as accept

Given Fraud check is negative and status is accept
When UI calls OpaSaving with valid request
Then OpaSaving returns application status and asm score as accept for the customer

Scenario: 2 OpaSaving returns ASM status as refer

Given Fraud check is negative and status is refer
When UI calls OpaSaving with valid request
Then OpaSaving returns application status and asm score as refer for the customer


Scenario: 3 OpaSaving returns ASM status as unscored

Given Fraud check is negative and status is refer and referral code is 501
When UI calls OpaSaving with valid request
Then OpaSaving returns application status as unscored for the customer

Scenario: 4 OpaSaving returns ASM status as decline

Given Fraud check is decline
When UI calls OpaSaving with valid request
Then OpaSaving returns application status as decline for the customer

