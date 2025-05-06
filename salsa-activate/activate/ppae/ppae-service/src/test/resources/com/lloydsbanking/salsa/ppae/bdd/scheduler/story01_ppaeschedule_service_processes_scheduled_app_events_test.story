Meta:

Narrative:
As a service consumer
I want PPAE Scheduler service to process scheduled appliaction events
So that I can process the pending applications

Scenario: 1. PPAE Scheduled job picks the pending applications to process

Given PPAE salsa switch is ON
And There are scheduled application events
When PPAE Scheduled Job runs
Then pending applications gets picked for processing





