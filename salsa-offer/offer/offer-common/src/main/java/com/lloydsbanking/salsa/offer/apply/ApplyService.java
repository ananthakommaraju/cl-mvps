package com.lloydsbanking.salsa.offer.apply;

import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.apply.convert.OfferToRpcRequestConverter;
import com.lloydsbanking.salsa.offer.apply.downstream.CreditScoreRetriever;
import com.lloydsbanking.salsa.offer.apply.downstream.FraudDecisionRetriever;
import com.lloydsbanking.salsa.offer.apply.downstream.ProductConditionsRetriever;
import com.lloydsbanking.salsa.offer.apply.evaluate.ApplicationStatusEvaluator;
import com.lloydsbanking.salsa.offer.apply.evaluate.IneligibleProductsEvaluator;
import com.lloydsbanking.salsa.offer.apply.evaluate.ProductOptionsEvaluator;
import com.lloydsbanking.salsa.offer.apply.evaluate.RuleConditionsEvaluator;
import com.lloydsbanking.salsa.offer.downstream.EligibilityRetriever;
import com.lloydsbanking.salsa.offer.eligibility.convert.OfferToEligibilityRequestConverter;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.lloydsbanking.salsa.soap.asm.f204.objects.F204Resp;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ApplyService {
    private static final String GROUP_CODE_MIN_OVERDRAFT_LIMIT = "MIN_OVERDRAFT_LIMIT";
    private static final String PROD_PACK_TYPE_NORMAL = "2001";
    private static final int INDEX_OF_CUSTOMER_SCORE_FOR_APPLY = 1;

    @Autowired
    ApplicationStatusEvaluator applicationStatusEvaluator;
    @Autowired
    FraudDecisionRetriever fraudDecisionRetriever;
    @Autowired
    CreditScoreRetriever creditScoreRetriever;
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    ProductOptionsEvaluator productOptionsEvaluator;
    @Autowired
    RuleConditionsEvaluator ruleConditionsEvaluator;
    @Autowired
    LookupDataRetriever offerLookupDataRetriever;
    @Autowired
    OfferToEligibilityRequestConverter offerToEligibilityRequestConverter;
    @Autowired
    IneligibleProductsEvaluator ineligibleProductsEvaluator;
    @Autowired
    EligibilityRetriever eligibilityRetriever;
    @Autowired
    OfferToRpcRequestConverter offerToRpcRequestConverter;
    @Autowired
    ProductConditionsRetriever productConditionsRetriever;

    public void applyCreditRatingScale(DepositArrangement productArrangement, RequestHeader requestHeader, boolean isBFPOIndicatorPresent, boolean isPPAECall) throws OfferException {
        productArrangement.getPrimaryInvolvedParty().getCustomerScore().add(INDEX_OF_CUSTOMER_SCORE_FOR_APPLY, new CustomerScore());
        CustomerScore customerScoreApply = productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(INDEX_OF_CUSTOMER_SCORE_FOR_APPLY);
        F204Resp f204Resp;
        try {
            f204Resp = retrieveFraudDecision(productArrangement, requestHeader);
        } catch (ExternalServiceErrorMsg | ResourceNotAvailableErrorMsg | ExternalBusinessErrorMsg errorMsg) {
            throw new OfferException(errorMsg);
        }
        if (f204Resp != null) {
            try {
                productArrangement.setApplicationStatus(applicationStatusEvaluator.getApplicationStatusForAsmFraudDecision(customerScoreApply, f204Resp, requestHeader));
            } catch (DataNotAvailableErrorMsg dataNotAvailableErrorMsg) {
                throw new OfferException(dataNotAvailableErrorMsg);
            }
            setF205Response(productArrangement, requestHeader, customerScoreApply, isBFPOIndicatorPresent, isPPAECall);
        }
    }

    private void setF205Response(DepositArrangement productArrangement, RequestHeader requestHeader, CustomerScore customerScoreApply, boolean isBFPOIndicatorPresent, boolean isPPAECall) throws OfferException {
        String previousApplicationStatus = productArrangement.getApplicationStatus();
        if (!ArrangementType.SAVINGS.getValue().equals(productArrangement.getArrangementType()) && !ApplicationStatus.DECLINED.getValue()
                .equals(previousApplicationStatus) && !ApplicationStatus.UNSCORED.getValue().equals(previousApplicationStatus)) {
            F205Resp f205Resp = creditScoreRetriever.retrieveCreditDecision(productArrangement, requestHeader);
            if (f205Resp != null) {
                try {
                    productArrangement.setApplicationStatus(applicationStatusEvaluator.getApplicationStatusForCreditScoreDecision(f205Resp, previousApplicationStatus, customerScoreApply, requestHeader));
                } catch (DataNotAvailableErrorMsg dataNotAvailableErrorMsg) {
                    throw new OfferException(dataNotAvailableErrorMsg);
                }
                if (ApplicationStatus.APPROVED.getValue().equals(productArrangement.getApplicationStatus()) || ApplicationStatus.REFERRED.getValue()
                        .equals(productArrangement.getApplicationStatus())) {
                    RetrieveProductConditionsRequest retrieveProductConditionsRequest = offerToRpcRequestConverter.convertOfferToRpcRequest(f205Resp, requestHeader);
                    RetrieveProductConditionsResponse retrieveProductConditionsResponse = productConditionsRetriever.retrieveRPCResponse(retrieveProductConditionsRequest, productArrangement
                            .getAssociatedProduct());
                    if (isPPAECall) {
                        productArrangement.getOfferedProducts().addAll(retrieveProductConditionsResponse.getProduct());
                    } else {
                        setOfferedProducts(productArrangement, retrieveProductConditionsResponse, requestHeader, isBFPOIndicatorPresent);
                        List<ProductOptions> productOptions = getProductOptionses(requestHeader, f205Resp);
                        List<RuleCondition> ruleConditionList = getRuleConditionList(productArrangement);
                        productArrangement.setOverdraftDetails(new OverdraftDetails());
                        productArrangement.setIsOverdraftRequired(ruleConditionsEvaluator.setFacilitiesOffered(productOptions, productArrangement.getOverdraftDetails(), ruleConditionList, getOverdraftLimit(requestHeader)));
                    }
                }
            }
        }
    }

    private List<ProductOptions> getProductOptionses(RequestHeader requestHeader, F205Resp f205Resp) throws
            OfferException {
        List<ProductOptions> productOptions;
        try {
            productOptions = productOptionsEvaluator.getProductOptions(f205Resp, requestHeader.getChannelId());
        } catch (DataNotAvailableErrorMsg dataNotAvailableErrorMsg) {
            throw new OfferException(dataNotAvailableErrorMsg);
        }
        return productOptions;
    }

    private void setOfferedProducts(DepositArrangement productArrangement, RetrieveProductConditionsResponse retrieveProductConditionsResponse, RequestHeader requestHeader, boolean isBFPOIndicatorPresent) throws OfferException {
        if (isExtSysProdIdentifierPresent(retrieveProductConditionsResponse)) {
            if (!(retrieveProductConditionsResponse.getProduct().get(0).getProductoffer().isEmpty())) {
                String offerType = retrieveProductConditionsResponse.getProduct().get(0).getProductoffer().get(0).getOfferType();
                if (!(PROD_PACK_TYPE_NORMAL.equalsIgnoreCase(offerType))) {
                    DetermineEligibleCustomerInstructionsRequest eligibilityRequest = offerToEligibilityRequestConverter.convertOfferToEligibilityForOfferedProducts(productArrangement, requestHeader, retrieveProductConditionsResponse
                            .getProduct(), isBFPOIndicatorPresent);
                    DetermineEligibleCustomerInstructionsResponse eligibilityResponse;
                    try {
                        eligibilityResponse = eligibilityRetriever.callEligibilityService(eligibilityRequest);
                    } catch (DataNotAvailableErrorMsg | InternalServiceErrorMsg | ResourceNotAvailableErrorMsg errorMsg) {
                        throw new OfferException(errorMsg);
                    }
                    List<Product> offeredProducts = ineligibleProductsEvaluator.filterIneligibleProducts(retrieveProductConditionsResponse.getProduct(), eligibilityResponse);
                    productArrangement.getOfferedProducts().addAll(offeredProducts);
                } else {
                    productArrangement.getOfferedProducts().addAll(retrieveProductConditionsResponse.getProduct());
                }
            }
        }
    }

    private boolean isExtSysProdIdentifierPresent(RetrieveProductConditionsResponse retrieveProductConditionsResponse) {
        return retrieveProductConditionsResponse != null && !CollectionUtils.isEmpty(retrieveProductConditionsResponse.getProduct()) && !CollectionUtils.isEmpty(retrieveProductConditionsResponse
                .getProduct()
                .get(0)
                .getExternalSystemProductIdentifier());
    }

    private List<RuleCondition> getRuleConditionList(DepositArrangement productArrangement) {
        List<RuleCondition> ruleConditionList;
        if (productArrangement.getConditions() != null) {
            ruleConditionList = productArrangement.getConditions();
        } else {
            ruleConditionList = new ArrayList<>();
            productArrangement.getConditions().addAll(ruleConditionList);
        }
        return ruleConditionList;
    }

    private String getOverdraftLimit(RequestHeader requestHeader) throws OfferException {
        List<ReferenceDataLookUp> lookupList;
        try {
            List<String> groupCodeList = new ArrayList<>();
            groupCodeList.add(GROUP_CODE_MIN_OVERDRAFT_LIMIT);
            lookupList = offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(requestHeader.getChannelId(), groupCodeList);
        } catch (DataNotAvailableErrorMsg dataNotAvailableErrorMsg) {
            throw new OfferException(dataNotAvailableErrorMsg);
        }
        for (ReferenceDataLookUp lookUpData : lookupList) {
            if (GROUP_CODE_MIN_OVERDRAFT_LIMIT.equals(lookUpData.getGroupCode()) && lookUpData.getLookupValueDesc() != null) {
                return lookUpData.getLookupValueDesc();
            }
        }
        return null;
    }

    private F204Resp retrieveFraudDecision(ProductArrangement productArrangement, RequestHeader header) throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg {
        F204Resp f204Resp = null;
        String regionCode = null, areaCode = null;
        if (productArrangement.getFinancialInstitution() != null && !productArrangement.getFinancialInstitution().getHasOrganisationUnits().isEmpty()) {
            regionCode = productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getRegionCode();
            areaCode = productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getAreaCode();
        }
        String contactPointId = headerRetriever.getContactPoint(header).getContactPointId();
        if (productArrangement.getPrimaryInvolvedParty() != null && productArrangement.getAssociatedProduct() != null && productArrangement.getPrimaryInvolvedParty()
                .getIsPlayedBy() != null) {
            f204Resp = fraudDecisionRetriever.getFraudDecision(regionCode, areaCode, contactPointId, productArrangement.getAssociatedProduct()
                    .getExternalSystemProductIdentifier(), productArrangement.getArrangementId(), productArrangement.getPrimaryInvolvedParty()
                    .getIsPlayedBy()
                    .getIndividualName(), productArrangement.getPrimaryInvolvedParty(), productArrangement.getPrimaryInvolvedParty().getPostalAddress(), header);
        }
        return f204Resp;
    }
}