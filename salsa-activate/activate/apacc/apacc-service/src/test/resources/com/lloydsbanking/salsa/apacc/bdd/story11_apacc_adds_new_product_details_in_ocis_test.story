Meta:

Narrative:
Activate Product Arrangement adds new product details in OCIS

Scenario: 1. APACC adds a new product in OCIS
Given card account is created
And F259 responds successfully
When there is a call to APACC
Then APACC adds the product successfully

Scenario: 2. APACC does not add the product in OCIS
Given card account is created
And F259 responds with error
When there is a call to APACC
Then APACC does not add the product and continues

