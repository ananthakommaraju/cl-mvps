Meta:

Narrative:
As the loans journey I want OPALOANS to check customer's existence on OCIS and customer's KYC compliance.

Scenario: 1 OPALOANS returns customer details and existing products when customer's birth date in request is matched uniquely with birth date returned by C216 and customer is KYC compliant

Given related application id and BFPO address indicator are not present
And OCIS ID is not present in request
And customer is found with birth date matched uniquely with birth date from C216
And customer KYC compliance is true
And verde switch is ON
When UI calls OPALOANS with valid request
Then OPALOANS returns new customer indicator as false in response
And OPALOANS returns customer details and existing products in response

Scenario: 2 OPALOANS returns customer details and existing products when customer's birth date in request is matched uniquely with birth date returned by C216 and customer is not KYC compliant

Given related application id and BFPO address indicator are not present
And OCIS ID is not present in request
And customer is found with birth date matched uniquely with birth date from C216
And customer KYC compliance is false
And verde switch is ON
When UI calls OPALOANS with valid request
Then OPALOANS returns new customer indicator as false in response
And OPALOANS returns customer details and existing products in response

Scenario: 3 OPALOANS returns customer details and existing products when customer's name in request is matched with name returned by C216 and customer is not KYC compliant

Given related application id and BFPO address indicator are not present
And OCIS ID is not present in request
And customer is found with birth date not matched uniquely with birth date from C216 and customer name in request matched with name returned by C216
And customer KYC compliance is false
And verde switch is ON
When UI calls OPALOANS with valid request
Then OPALOANS returns new customer indicator as false in response
And OPALOANS returns customer details and existing products in response

Scenario: 4 OPALOANS returns reason code as 01 when customer's name in request is not matched with name returned by C216

Given related application id and BFPO address indicator are not present
And OCIS ID is not present in request
And customer is found with birth date not matched uniquely with birth date from C216 and customer name in request is not matched with name returned by C216
When UI calls OPALOANS with valid request
Then OPALOANS returns reason code as 01 and description as FirstName and LastName Not Matched with OCIS in response

Scenario: 5 OPALOANS returns condition with name as ADDITIONAL_DATA_REQUIRED_INDICATOR when customer's name is not present in request

Given related application id and BFPO address indicator are not present
And OCIS ID is not present in request
And customer is found with birth date not matched uniquely with birth date from C216 and customer name is not present in request
When UI calls OPALOANS with valid request
Then OPALOANS returns condition with name as ADDITIONAL_DATA_REQUIRED_INDICATOR and result as true in response

Scenario: 6 OPALOANS responds with external business error when customer's birth date in request is not matched with birth date returned by C216

Given related application id and BFPO address indicator are not present
And OCIS ID is not present in request
And customer is found with birth date not matched with birth date from C216
When UI calls OPALOANS with valid request
Then OPALOANS returns reason code as 01 and description as BirthDate Not Matched in response