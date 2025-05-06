Meta:

Narrative:
As the loans journey I want OPALOANS to terminate or continue based on customer's existence and eligibility status returned by Determine Eligible Customer Instructions Service
and LRA switch status and saved loan with or without duplicate status.

Scenario: 1 OPALOANS terminates when customer's eligible loan products do not exist

Given related application id and BFPO address indicator are not present
And customer is existing
And B231 responded without eligible loan products
When UI calls OPALOANS with valid request
Then OPALOANS returns reason code as 02 and description as No Eligible Loan Products in response
And OPALOANS returns rule condition with name ELIGIBILITY and result N in response
And OPALOANS clears eligible and existing products
And OPALOANS does not return user type and party role and internal user identifier in response and terminates

Scenario: 2 OPALOANS terminates when customer is not existing with additional data indicator true

Given related application id and BFPO address indicator are not present
And customer is not existing with additional data indicator true
When UI calls OPALOANS with valid request
Then OPALOANS returns rule condition with name ADDITIONAL_DATA_REQUIRED_INDICATOR and result true in response
And OPALOANS does not return user type and party role and internal user identifier in response and terminates

Scenario: 3 OPALOANS terminates when customer is not existing with additional data indicator false

Given related application id and BFPO address indicator are not present
And customer is not existing with additional data indicator false
When UI calls OPALOANS with valid request
Then OPALOANS returns reason code as 01 and description as BirthDate Not Matched in response
And OPALOANS returns rule condition with name ELIGIBILITY and result N in response
And OPALOANS clears eligible and existing products
And OPALOANS does not return user type and party role and internal user identifier in response and terminates

Scenario: 4 OPALOANS continues when LRA switch is OFF and eligibility status returned by DECI is true

Given related application id and BFPO address indicator are not present
And customer is existing
And B231 responded with eligible loan products
And LRA switch is OFF
And DECI responded with eligibility status as true and reason code as 0 and description as null
When UI calls OPALOANS with valid request
Then OPALOANS returns rule condition with name ELIGIBILITY and result Y in response
And OPALOANS does not clear eligible and existing products
And OPALOANS returns user type and party role and internal user identifier in response and continues

Scenario: 5 OPALOANS terminates when LRA switch is OFF and eligibility status returned by DECI is false for rule CR046

Given related application id and BFPO address indicator are not present
And customer is existing
And B231 responded with eligible loan products
And LRA switch is OFF
And DECI responded with eligibility status as false and reason code as CR046 and description as Post returned from this address
When UI calls OPALOANS with valid request
Then OPALOANS returns reason code as CR046 and description as Post returned from this address in response
And OPALOANS returns rule condition with name ELIGIBILITY and result N in response
And OPALOANS does not clear eligible and existing products
And OPALOANS does not return user type and party role and internal user identifier in response and terminates

Scenario: 6 OPALOANS terminates when LRA switch is OFF and eligibility status returned by DECI is false for rule CR047

Given related application id and BFPO address indicator are not present
And customer is existing
And B231 responded with eligible loan products
And LRA switch is OFF
And DECI responded with eligibility status as false and reason code as CR047 and description as Address Validation Failed
When UI calls OPALOANS with valid request
Then OPALOANS returns reason code as CR047 and description as Address Validation Failed in response
And OPALOANS returns rule condition with name ELIGIBILITY and result N in response
And OPALOANS does not clear eligible and existing products
And OPALOANS does not return user type and party role and internal user identifier in response and terminates

Scenario: 7 OPALOANS continues when LRA switch is ON and eligibility status returned by DECI is true

Given related application id and BFPO address indicator are not present
And customer is existing
And B231 responded with eligible loan products
And LRA switch is ON and duplicate saved loan status is false
And DECI responded with eligibility status as true and reason code as 0 and description as null
When UI calls OPALOANS with valid request
Then OPALOANS returns rule condition with name ELIGIBILITY and result Y in response
And OPALOANS does not clear eligible and existing products
And OPALOANS returns user type and party role and internal user identifier in response and continues

Scenario: 8 OPALOANS terminates when LRA switch is ON and eligibility status returned by DECI is false for rule CR047

Given related application id and BFPO address indicator are not present
And customer is existing
And B231 responded with eligible loan products
And LRA switch is ON and duplicate saved loan status is false
And DECI responded with eligibility status as false and reason code as CR047 and description as Address Validation Failed
When UI calls OPALOANS with valid request
Then OPALOANS returns reason code as CR047 and description as Address Validation Failed in response
And OPALOANS returns rule condition with name ELIGIBILITY and result N in response
And OPALOANS does not clear eligible and existing products
And OPALOANS does not return user type and party role and internal user identifier in response and terminates

Scenario: 9 OPALOANS terminates when LRA switch is ON and duplicate saved loan exists

Given related application id and BFPO address indicator are not present
And customer is existing
And B231 responded with eligible loan products
And LRA switch is ON and duplicate saved loan status is true
When UI calls OPALOANS with valid request
Then OPALOANS returns reason code as 05 and description as Already Have Saved Application in response
And OPALOANS returns rule condition with name ELIGIBILITY and result N in response
And OPALOANS clears eligible and existing products
And OPALOANS updates score identifier in customer score
And OPALOANS does not return user type and party role and internal user identifier in response and terminates