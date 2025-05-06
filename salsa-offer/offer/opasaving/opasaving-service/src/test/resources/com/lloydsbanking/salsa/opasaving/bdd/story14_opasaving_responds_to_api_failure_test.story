Meta:

Narrative:
OfferProductArrangementSaving responds to API failure.

Scenario: 1 Opasaving throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given OCIS F447 is not available
When UI calls Opasaving with valid request
Then Opasaving throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 2 Opasaving throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given OCIS F336 is not available
When UI calls Opasaving with valid request
Then Opasaving throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 3 Opasaving throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given OCIS F061 is not available
When UI calls Opasaving with valid request
Then Opasaving throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 4 Opasaving throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given DECI is not available
When UI calls Opasaving with valid request
Then Opasaving throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 5 Opasaving throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given F204 is not available
When UI calls Opasaving with valid request
Then Opasaving throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 6 Opasaving throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given RPC is not available
When UI calls Opasaving with valid request
Then Opasaving throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 7 Opasaving throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given F062 is not available
When UI calls Opasaving with valid request
Then Opasaving throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 8 Opasaving throws OfferProductArrangementExternalBusinessErrorMsg In response

Given F447 returns error code in response
When UI calls Opasaving with valid request
Then Opasaving throws OfferProductArrangementExternalBusinessErrorMsg In response

Scenario: 9 Opasaving throws OfferProductArrangementExternalServiceErrorMsg In response

Given F336 returns error code in response
When UI calls Opasaving with valid request
Then Opasaving throws OfferProductArrangementExternalServiceErrorMsg In response

Scenario: 10 Opasaving throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given DCPC is not available
When UI calls Opasaving with valid request
Then Opasaving throws OfferProductArrangementResourceNotAvailableErrorMsg In response



