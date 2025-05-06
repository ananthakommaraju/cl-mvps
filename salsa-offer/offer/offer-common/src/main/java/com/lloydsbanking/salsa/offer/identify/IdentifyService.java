package com.lloydsbanking.salsa.offer.identify;

import com.lloydsbanking.salsa.constant.CustomerSegment;
import com.lloydsbanking.salsa.logging.application.CustomerTraceLog;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import com.lloydsbanking.salsa.offer.ApplicantType;
import com.lloydsbanking.salsa.offer.EIDVStatus;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.identify.convert.F061ToInvolvedPartyDetailsResponseConverter;
import com.lloydsbanking.salsa.offer.identify.downstream.InvolvedPartyMatchRetriever;
import com.lloydsbanking.salsa.offer.identify.downstream.InvolvedPartyRetriever;
import com.lloydsbanking.salsa.offer.identify.downstream.ProductHoldingRetriever;
import com.lloydsbanking.salsa.offer.identify.evaluate.KYCStatusEvaluator;
import com.lloydsbanking.salsa.offer.identify.utility.CustomerUtility;
import com.lloydsbanking.salsa.offer.identify.utility.DeliveryPointSuffixAnalyser;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Resp;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Resp;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.Product;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IdentifyService {
    private static final Logger LOGGER = Logger.getLogger(IdentifyService.class);
    private static final String ASSESSMENT_TYPE_EIDV = "EIDV";
    private static final String PRODUCT_STATUS_ACTIVE = "001";

    private DeliveryPointSuffixAnalyser deliveryPointSuffixAnalyser;
    private InvolvedPartyMatchRetriever involvedPartyMatchRetriever;
    private ProductHoldingRetriever productHoldingRetriever;
    private InvolvedPartyRetriever involvedPartyRetriever;
    private CustomerUtility customerUtility;
    private KYCStatusEvaluator kycStatusEvaluator;
    private F061ToInvolvedPartyDetailsResponseConverter f061ToInvolvedPartyDetailsResponseConverter;
    private CustomerTraceLog customerTraceLog;
    private ProductTraceLog productTraceLog;

    @Autowired
    public IdentifyService(InvolvedPartyRetriever involvedPartyRetriever, ProductHoldingRetriever productHoldingRetriever, DeliveryPointSuffixAnalyser deliveryPointSuffixAnalyser, InvolvedPartyMatchRetriever involvedPartyMatchRetriever, CustomerUtility customerUtility, KYCStatusEvaluator kycStatusEvaluator, F061ToInvolvedPartyDetailsResponseConverter f061ToInvolvedPartyDetailsResponseConverter, CustomerTraceLog customerTraceLog, ProductTraceLog productTraceLog) {
        this.deliveryPointSuffixAnalyser = deliveryPointSuffixAnalyser;
        this.involvedPartyMatchRetriever = involvedPartyMatchRetriever;
        this.productHoldingRetriever = productHoldingRetriever;
        this.involvedPartyRetriever = involvedPartyRetriever;
        this.customerUtility = customerUtility;
        this.kycStatusEvaluator = kycStatusEvaluator;
        this.f061ToInvolvedPartyDetailsResponseConverter = f061ToInvolvedPartyDetailsResponseConverter;
        this.customerTraceLog = customerTraceLog;
        this.productTraceLog = productTraceLog;
    }

    public List<Product> identifyInvolvedParty(RequestHeader header, Customer customer) throws OfferException {
        LOGGER.info(customerTraceLog.getCustomerTraceEventMessage(customer, "Entering IdentifyInvolvedPartyDetails (To match the customer, retrieve its Product Holdings and check its KYC status) "));
        customer.getCustomerScore().clear();
        boolean isAuthCustomer = false;
        List<Product> productHoldings = null;
        //TODO- to check usage of customer score in request
        if (!StringUtils.isEmpty(customer.getCustomerIdentifier())) {
            LOGGER.info("Customer Identifier in request: " + customer.getCustomerIdentifier());
            assignCustomerSegment(customer);
            isAuthCustomer = true;
            customer.setIsAuthCustomer(isAuthCustomer);
        } else if (deliveryPointSuffixAnalyser.isDeliveryPointSuffixPresent(customer.getPostalAddress())) {
            isAuthCustomer = false;
            customer.setIsAuthCustomer(isAuthCustomer);
            matchCustomer(customer, header);
        }
        if (null != customer.getCustomerIdentifier()) {
            LOGGER.info("Customer is found on OCIS with Customer Identifier: " + customer.getCustomerIdentifier());
            productHoldings = getCustomerHoldingsSegmentAndCustomerNumber(header, customer);
            updateCustomerDetailsFromOcis(header, isAuthCustomer, customer, productHoldings);
        } else {
            LOGGER.info("Customer is not found on OCIS");
            customer.setCustomerSegment(CustomerSegment.NON_FRANCHISED.getValue());
            customer.setNewCustomerIndicator(true);
            customer.getIsPlayedBy().setIsStaffMember(false);
            isAuthCustomer = false;
            customer.setIsAuthCustomer(isAuthCustomer);
        }
        List<Product> activeProductHoldings = new ArrayList<>();
        if (!CollectionUtils.isEmpty(productHoldings)) {
            for (Product product : productHoldings) {
                if (PRODUCT_STATUS_ACTIVE.equals(product.getStatusCode())) {
                    activeProductHoldings.add(product);
                }
            }
            LOGGER.info(productTraceLog.getProdListTraceEventMessage(activeProductHoldings, "Exiting IdentifyInvolvedPartyDetails (Customer Product Holdings received) "));
        }
        LOGGER.info(customerTraceLog.getCustomerTraceEventMessage(customer, "Exiting IdentifyInvolvedPartyDetails (Customer Details received) "));
        return activeProductHoldings;
    }

    private void assignCustomerSegment(Customer customer) {
        if (StringUtils.isEmpty(customer.getCustomerSegment())) {
            customer.setCustomerSegment(CustomerSegment.FRANCHISED.getValue());
        }
    }

    private void matchCustomer(Customer customer, RequestHeader header) throws OfferException {
        LOGGER.info(customerTraceLog.getCustomerTraceEventMessage(customer, "Entering MatchInvolvedParty (OCIS F447) "));
        F447Resp involvedPartyMatch = null;
        try {
            involvedPartyMatch = involvedPartyMatchRetriever.getInvolvedPartyMatch(customer.getPostalAddress(), customer.getIsPlayedBy(), header);
        } catch (ResourceNotAvailableErrorMsg | ExternalBusinessErrorMsg | InternalServiceErrorMsg e) {
            throw new OfferException(e);
        }
        if (involvedPartyMatch.getPartyId() > 0) {
            customer.setCustomerIdentifier(String.valueOf(involvedPartyMatch.getPartyId()));
            customer.setCidPersID(involvedPartyMatch.getCIDPersId());
        }
        LOGGER.info("Exiting MatchInvolvedParty (OCIS F447) with CIDPers ID: " + involvedPartyMatch.getCIDPersId() + " and Party ID: " + involvedPartyMatch.getPartyId());
    }

    private List<Product> getCustomerHoldingsSegmentAndCustomerNumber(RequestHeader header, Customer customer) throws OfferException {
        customer.setNewCustomerIndicator(false);
        List<Product> productHoldings = null;
        try {
            productHoldings = productHoldingRetriever.getProductHoldings(header, customer.getCustomerIdentifier());
        } catch (ExternalServiceErrorMsg | ResourceNotAvailableErrorMsg externalServiceErrorMsg) {
            throw new OfferException(externalServiceErrorMsg);
        }
        if (null != customer.isIsAuthCustomer() && !customer.isIsAuthCustomer()) {
            customer.setCustomerSegment(customerUtility.getCustomerSegment(productHoldings));
        }
        customer.setCbsCustomerNumber(customerUtility.getCBSCustomerNumber(productHoldings));
        customer.setCustomerNumber(customerUtility.getFDICustomerID(productHoldings));
        return productHoldings;
    }

    private void updateCustomerDetailsFromOcis(RequestHeader header, boolean isAuthCustomer, Customer customer, List<Product> productHoldings) throws OfferException {
        LOGGER.info("Entering RetrieveInvolvedPartyDetails (OCIS F061) with Customer Identifier: " + customer.getCustomerIdentifier());
        F061Resp f061Resp = null;
        try {
            f061Resp = involvedPartyRetriever.retrieveInvolvedPartyDetails(header, customer.getCustomerIdentifier());
            f061ToInvolvedPartyDetailsResponseConverter.setInvolvedPartyDetailsResponse(customer, isAuthCustomer, f061Resp);
        } catch (InternalServiceErrorMsg | ExternalServiceErrorMsg | ResourceNotAvailableErrorMsg | ExternalBusinessErrorMsg internalServiceErrorMsg) {
            throw new OfferException(internalServiceErrorMsg);
        }

        LOGGER.info(customerTraceLog.getCustomerTraceEventMessage(customer, "Exiting RetrieveInvolvedPartyDetails (OCIS F061) "));

        if (kycStatusEvaluator.isKycCompliant(customer, productHoldings, f061Resp.getPartyEnqData())) {
            setCustomerScoreForEidv(customer);
        }
    }

    private void setCustomerScoreForEidv(Customer customer) {
        CustomerScore customerScoreEidv = new CustomerScore();
        customerScoreEidv.setAssessmentType(ASSESSMENT_TYPE_EIDV);
        customerScoreEidv.setScoreResult(isCustomerChild(customer.getApplicantType()) ? "N/A" : EIDVStatus.ACCEPT.getValue());
        customer.getCustomerScore().add(0, customerScoreEidv);
    }

    private boolean isCustomerChild(String applicantType) {
        return ApplicantType.DEPENDENT.getValue().equals(applicantType);
    }
}
