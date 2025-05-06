Meta:

Narrative:
OfferProductArrangementCreditCard responds to API failure.

Scenario: 1 OPACC throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given OCIS F447 is not available
When UI calls OPACC with valid request
Then OPACC throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 2 OPACC throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given OCIS F336 is not available
When UI calls OPACC with valid request
Then OPACC throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 3 OPACC throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given OCIS F061 is not available
When UI calls OPACC with valid request
Then OPACC throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 4 OPACC throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given DECI is not available
When UI calls OPACC with valid request
Then OPACC throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 5 OPACC throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given F424 is not available
When UI calls OPACC with valid request
Then OPACC throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 6 OPACC throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given Data Encrypt Service is not available
When UI calls OPACC with valid request
Then OPACC throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 7 OPACC throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given RPC is not available
When UI calls OPACC with valid request
Then OPACC throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 8 OPACC throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given F424 is not available
When UI calls OPACC with valid request
Then OPACC throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 9 OPACC throws OfferProductArrangementExternalServiceErrorMsg In response

Given F424 return external service error code
When UI calls OPACC with valid request
Then OPACC throws OfferProductArrangementExternalServiceErrorMsg In response

Scenario: 10 OPACC throws OfferProductArrangementExternalBusinessErrorMsg In response

Given F424 return external business error code
When UI calls OPACC with valid request
Then OPACC throws OfferProductArrangementExternalBusinessErrorMsg In response








