Meta:

Narrative:
As the unauth current account journey I want to return a decision using the ASM score, Eidv score and Sira score on the application
and retrieve the product offers (upsell/downsell)

Scenario: 1 OPAPCA returns application status as approved and sub status as null

Given Sira decision is accept
And EIDV Score is accept
And ASM decision is accept
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and total rule score as accept for the customer

Scenario: 2 OPAPCA returns application status as referred and sub status as Sira refer

Given Sira decision is refer Fraud
And EIDV Score is accept
And ASM decision is accept
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and total rule score as refer fraud for the customer

Scenario: 3 OPAPCA returns application status as AwaitingManualIDV and sub status as Sira referIDV

Given Sira decision is refer IDV
And EIDV Score is accept
And ASM decision is accept
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and total rule score as refer idv for the customer

Scenario: 4 OPAPCA returns application status as Decline and sub status as Sira decline

Given Sira decision is decline
And EIDV Score is accept
And ASM decision is accept
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and total rule score as decline for the customer

Scenario: 5 OPAPCA returns application status as refer and sub status as ASM refer

Given Sira decision is accept
And EIDV Score is accept
And ASM decision is refer
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and application sub status as ASM refer for the customer

Scenario: 6 OPAPCA returns application status as refer and sub status as Sira and ASM refer

Given Sira decision is refer Fraud
And EIDV Score is accept
And ASM decision is refer
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and application sub status as Sira and Asm refer for the customer

Scenario: 7 OPAPCA returns application status as refer and sub status as ASM refer

Given Sira decision is refer IDV
And EIDV Score is accept
And ASM decision is refer
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and application sub status as Asm refer for the customer


Scenario: 8 OPAPCA returns application status as decline and sub status as Sira decline

Given Sira decision is decline
And EIDV Score is accept
And ASM decision is refer
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and application sub status as Sira decline for the customer

Scenario: 9 OPAPCA returns application status as decline and sub status as ASM decline

Given Sira decision is accept
And EIDV Score is accept
And ASM decision is decline
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and application sub status as ASM decline for the customer

Scenario: 10 OPAPCA returns application status as decline and sub status as ASM decline

Given Sira decision is refer Fraud
And EIDV Score is accept
And ASM decision is decline
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and application sub status as ASM decline for the customer


Scenario: 11 OPAPCA returns application status as decline and sub status as ASM decline

Given Sira decision is refer IDV
And EIDV Score is accept
And ASM decision is decline
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and application sub status as ASM decline for the customer

Scenario: 12 OPAPCA returns application status as decline and sub status as ASM decline

Given Sira decision is decline
And EIDV Score is accept
And ASM decision is decline
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and application sub status as Sira decline for the customer

Scenario: 13 OPAPCA returns application status as approved and sub status as null

Given Sira returns error
And EIDV Score is accept
And ASM decision is accept
When UI calls OPAPCA with valid request
Then OPAPCA returns application status and application sub status as null and fault code in the response


