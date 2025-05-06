Meta:

Narrative:
OfferProductArrangementPCA responds to API failure.

Scenario: 1 OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given OCIS F447 is not available
When UI calls OPAPCA with valid request
Then OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 2 OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given OCIS F336 is not available
When UI calls OPAPCA with valid request
Then OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 3 OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given OCIS F061 is not available
When UI calls OPAPCA with valid request
Then OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 4 OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given DECI is not available
When UI calls OPAPCA with valid request
Then OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 5 OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given F204 is not available
When UI calls OPAPCA with valid request
Then OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 6 OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given F205 is not available
When UI calls OPAPCA with valid request
Then OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 7 OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given RPC is not available
When UI calls OPAPCA with valid request
Then OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 8 OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given F062 is not available
When UI calls OPAPCA with valid request
Then OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 9 OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Given X711 is not available
When UI calls OPAPCA with valid request
Then OPAPCA throws OfferProductArrangementResourceNotAvailableErrorMsg In response

Scenario: 10 OPAPCA throws OfferProductArrangementExternalBusinessErrorMsg In response

Given F447 returns error code in response
When UI calls OPAPCA with valid request
Then OPAPCA throws OfferProductArrangementExternalBusinessErrorMsg In response

Scenario: 11 OPAPCA throws OfferProductArrangementExternalServiceErrorMsg In response

Given F336 returns error code in response
When UI calls OPAPCA with valid request
Then OPAPCA throws OfferProductArrangementExternalServiceErrorMsg In response

Scenario: 12 OPAPCA throws offerProductArrangementExternalServiceErrorMsg In response

Given F061 returns error code in response
When UI calls OPAPCA with valid request
Then OPAPCA throws OfferProductArrangementExternalServiceErrorMsg In response

Scenario: 13 OPAPCA throws offerProductArrangementExternalBusinessErrorMsg In response

Given F061 returns improper response
When UI calls OPAPCA with valid request
Then OPAPCA throws OfferProductArrangementExternalBusinessErrorMsg In response

Scenario: 14 OPAPCA throws offerProductArrangementExternalBusinessErrorMsg In response

Given F204 returns external business error code in response
When UI calls OPAPCA with valid request
Then OPAPCA throws OfferProductArrangementExternalBusinessErrorMsg In response

Scenario: 15 OPAPCA throws offerProductArrangementExternalServiceErrorMsg In response

Given F204 returns external service error code in response
When UI calls OPAPCA with valid request
Then OPAPCA throws OfferProductArrangementExternalServiceErrorMsg In response

Scenario: 16 OPAPCA throws offerProductArrangementExternalBusinessErrorMsg In response

Given F205 returns external business error code in response
When UI calls OPAPCA with valid request
Then OPAPCA throws OfferProductArrangementExternalBusinessErrorMsg In response

Scenario: 17 OPAPCA throws offerProductArrangementExternalServiceErrorMsg In response

Given F205 returns external service error code in response
When UI calls OPAPCA with valid request
Then OPAPCA throws OfferProductArrangementExternalServiceErrorMsg In response

Scenario: 18 OPAPCA throws OfferProductArrangementInternalServiceErrorMsg In response

Given F205 returns internal service error code in response
When UI calls OPAPCA with valid request
Then OPAPCA throws OfferProductArrangementInternalServiceErrorMsg In response



