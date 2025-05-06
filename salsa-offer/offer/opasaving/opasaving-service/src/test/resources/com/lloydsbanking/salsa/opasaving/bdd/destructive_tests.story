Meta:
@Destructive

Narrative:
A collection of tests which enter unexpected or malformed data in a Salsa service's request

Scenario: An invalid email email address is provided in the request
Given an invalid email address is given in the request to a Salsa service
When the UI calls OPASaving
Then OPASaving returns valid response

Scenario: OPASaving should return an error when there is no Product Arrangement in the request
Given the UI prepares a request without a Product Arrangement set
When the UI calls OPASaving
Then OPASaving throws an Internal Service Error

Scenario: OPASaving should return an error when there is no Primary Involved Party in the request
Given the UI prepares a request without a Primary Involved Party set
When the UI calls OPASaving
Then OPASaving throws an Internal Service Error