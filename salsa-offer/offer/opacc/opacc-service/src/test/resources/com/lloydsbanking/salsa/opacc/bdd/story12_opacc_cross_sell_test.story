Meta:

Narrative:
As the credit card journey I want to respond to cross sell request to return a credit decision and direct debit indicator using the ASM score on the application

Scenario: 1 OPACC returns ASM status as accept

Given related application id present in request
And Credit Decision is accept and Direct Debit is required
When UI calls OPACC with valid request
Then OPACC returns application status and asm score as accept
And direct debit indicator as true for the customer

Scenario: 2 OPACC returns ASM status as accept

Given related application id present in request
And Credit Decision is accept and Direct Debit is not required
When UI calls OPACC with valid request
Then OPACC returns application status and asm score as accept
And direct debit indicator as false for the customer

Scenario: 3 OPACC returns ASM status as unscored

Given related application id present in request
And Credit Decision is refer and Direct Debit is required and referral code is 501
When UI calls OPACC with valid request
Then OPACC returns application status and asm score as unscored
And direct debit indicator as true for the customer

Scenario: 4 OPACC returns ASM status as unscored

Given related application id present in request
And Credit Decision is refer and Direct Debit is not required and referral code is 501
When UI calls OPACC with valid request
Then OPACC returns application status and asm score as unscored
And direct debit indicator as false for the customer

Scenario: 5 OPACC returns ASM status as refer

Given related application id present in request
And Credit Decision is refer and Direct Debit is required and referral code is 601
When UI calls OPACC with valid request
Then OPACC returns application status and asm score as refer
And direct debit indicator as true for the customer

Scenario: 6 OPACC returns ASM status as refer

Given related application id present in request
And Credit Decision is refer and Direct Debit is not required and referral code is 601
When UI calls OPACC with valid request
Then OPACC returns application status and asm score as refer
And direct debit indicator as false for the customer

Scenario: 7 OPACC returns ASM status as decline

Given related application id present in request
And Credit Decision is decline
When UI calls OPACC with valid request
Then OPACC returns application status and asm score as decline
And direct debit indicator as false for the customer