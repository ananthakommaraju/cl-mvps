package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess;


import com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream.*;
import com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.evaluate.CustomerCardHolderNameEvaluator;
import com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.utility.CMASReferralReasons;
import com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.utility.OrderAccessCardDetails;
import com.lloydsbanking.salsa.soap.cmas.c808.objects.C808Req;
import com.lloydsbanking.salsa.soap.cmas.c808.objects.C808Resp;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.*;
import com.lloydsbanking.salsa.soap.cmas.c846.objects.C846Req;
import com.lloydsbanking.salsa.soap.cmas.c846.objects.C846Resp;
import com.lloydsbanking.salsa.soap.cmas.c846.objects.PlasticType;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderAccessService {
    private static final int CARD_ATHRSG_PARTY_EXT_SYSID = 0;

    private static final String CMAS_CARD_ATH_PARTY_TYP = "P";

    private static final int CARD_ORDER_STATUS_CODE_APPROVED = 1;

    private static final int CARD_ORDER_STATUS_CODE_REFFERED = 2;

    private static final int CARD_ORDER_STATUS_CODE_REJECTED = 3;

    private static final String ORDER_DECLINED_ORDER_IDENTIFIER = "0000000000";

    @Autowired
    InitiateCardOrderRetriever initiateCardOrderRetriever;

    @Autowired
    RetrieveEligibleCardsRetriver retrieveEligibleCardsRetriver;

    @Autowired
    CustomerCardHolderNameEvaluator customerCardHolderName;

    @Autowired
    OrderAccessCardDetails orderAccessCardDetails;

    @Autowired
    ValidateCardOrderRetriever validateCardOrderRetriever;

    public String orderAccessServiceResponse(String sortCode, String accountNumber, Customer primaryInvolvedParty, RequestHeader header) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        String orderIdentifier;

        final C808Req c808Request = C808Factory.createC808Request(sortCode, accountNumber, Long.valueOf(primaryInvolvedParty.getCustomerIdentifier()));
        C808Resp c808Resp = initiateCardOrderRetriever.getResponse(c808Request, header);

        String cCAApplicableIndicator = getCCAApplicableIndicator(c808Resp);
        String decisionText = getC808ResDecisionText(c808Resp);
        Integer decisionCode = getC808ResDecisionCode(c808Resp);
        String productIdentifier = c808Resp.getCardOrderNewAccount().getExtProdIdTx();
        int systemCode = c808Resp.getCardOrderNewAccount().getProdExtSysId();

        final C846Req c846Request = C846Factory.createC846Request(productIdentifier, cCAApplicableIndicator, decisionText, decisionCode, Long.valueOf(primaryInvolvedParty.getCustomerIdentifier()));
        C846Resp c846Resp = retrieveEligibleCardsRetriver.getResponse(c846Request, header);

        String cardHolderName = customerCardHolderName.getCardHolderName(c808Resp.getCardholderNew(), c846Resp.getPlasticTypes().getPlasticType().get(0));
        CardOrderNew cardOrderNew = getCardOrderNew(sortCode, accountNumber, primaryInvolvedParty, productIdentifier, systemCode, c846Resp.getPlasticTypes().getPlasticType().get(0), cardHolderName);
        int debitCardRenewalCode = getC808DebitCardRenewalCode(c808Resp);
        CardOrderCBSAddress cardOrderCBSAddress = getCardOrderCBSAddress(c808Resp, primaryInvolvedParty.getPostalAddress().get(0));
        CardOrderCBSData cardOrderCBSData = orderAccessCardDetails.getCardOrderCBSData(debitCardRenewalCode, cCAApplicableIndicator, decisionText, decisionCode);

        final C812Req c812Request = C812Factory.createC812Request(cardOrderNew, cardOrderCBSData, cardOrderCBSAddress);
        C812Resp c812Resp = validateCardOrderRetriever.getResponse(c812Request, header);

        int cardOrderStatusCd = 0;
        if (!c812Resp.getCardOrderReferralReasons().getCardOrderReferralReason().isEmpty()) {
            cardOrderStatusCd = getCardOrderStatus(c812Resp.getCardOrderReferralReasons());
        } else {
            cardOrderStatusCd = CARD_ORDER_STATUS_CODE_APPROVED;
        }
        if (cardOrderStatusCd != CARD_ORDER_STATUS_CODE_REJECTED) {
            orderIdentifier = String.valueOf(orderAccessCardDetails.fullFillCardOrder(sortCode, accountNumber, primaryInvolvedParty, header, cCAApplicableIndicator, productIdentifier, systemCode, c846Resp, cardHolderName, c812Resp, cardOrderStatusCd).getCardOrderId());
        } else {
            orderIdentifier = ORDER_DECLINED_ORDER_IDENTIFIER;
        }
        return orderIdentifier;
    }

    private int getCardOrderStatus(CardOrderReferralReasons cardOrderReferralReasons) {
        for (CardOrderReferralReason cardOrderReferralReason : cardOrderReferralReasons.getCardOrderReferralReason()) {
            final long cardOrderReferralReasonCd = cardOrderReferralReason.getCardOrderReferralReasonCd();

            if (CMASReferralReasons.PLASTIC_CONFLICT.equalsCode(cardOrderReferralReasonCd)) {
                return CARD_ORDER_STATUS_CODE_REJECTED;
            } else if (CMASReferralReasons.AUTH_RECEIVED.equalsCode(cardOrderReferralReasonCd) || CMASReferralReasons.AGREEMENT_AWAITED.equalsCode(cardOrderReferralReasonCd)) {
                return CARD_ORDER_STATUS_CODE_APPROVED;
            } else {
                return CARD_ORDER_STATUS_CODE_REFFERED;
            }

        }
        return CARD_ORDER_STATUS_CODE_REJECTED;
    }

    private CardOrderCBSAddress getCardOrderCBSAddress(C808Resp c808Resp, PostalAddress postalAddress) {
        CardOrderCBSAddress cardOrderCBSAddress = null;
        if (c808Resp != null && c808Resp.getCardOrderNewAccount() != null && c808Resp.getCardOrderNewAccount().getCardOrderCBSAddress() != null) {
            cardOrderCBSAddress = new CardOrderCBSAddress();
            cardOrderCBSAddress.setAddressLine1Tx40(c808Resp.getCardOrderNewAccount().getCardOrderCBSAddress().getAddressLine1Tx40());
            cardOrderCBSAddress.setAddressLine2Tx40(c808Resp.getCardOrderNewAccount().getCardOrderCBSAddress().getAddressLine2Tx40());
            cardOrderCBSAddress.setAddressLine3Tx40(c808Resp.getCardOrderNewAccount().getCardOrderCBSAddress().getAddressLine3Tx40());
            cardOrderCBSAddress.setAddressLine4Tx40(c808Resp.getCardOrderNewAccount().getCardOrderCBSAddress().getAddressLine4Tx40());
            cardOrderCBSAddress.setPostCd(c808Resp.getCardOrderNewAccount().getCardOrderCBSAddress().getPostCd());
        } else if (null != postalAddress && null != postalAddress.getUnstructuredAddress()) {
            cardOrderCBSAddress = new CardOrderCBSAddress();
            cardOrderCBSAddress.setAddressLine1Tx40(postalAddress.getUnstructuredAddress().getAddressLine1());
            cardOrderCBSAddress.setAddressLine2Tx40(postalAddress.getUnstructuredAddress().getAddressLine2());
            cardOrderCBSAddress.setAddressLine3Tx40(postalAddress.getUnstructuredAddress().getAddressLine3());
            cardOrderCBSAddress.setAddressLine4Tx40(postalAddress.getUnstructuredAddress().getAddressLine4());
            cardOrderCBSAddress.setPostCd(postalAddress.getUnstructuredAddress().getPostCode());
        }
        return cardOrderCBSAddress;
    }

    private CardOrderNew getCardOrderNew(String sortCode, String accountNumber, Customer primaryInvolvedParty, String productIdentifier, int systemCode, PlasticType plasticType, String cardHolderName) {
        CardOrderNew cardOrderNew = new CardOrderNew();
        cardOrderNew.setCardAuthorisingPartyId(Long.valueOf(primaryInvolvedParty.getCustomerIdentifier()));
        if (primaryInvolvedParty.getSourceSystemId() != null) {
            cardOrderNew.setCardAthrsgPartyExtSysId(Integer.valueOf(primaryInvolvedParty.getSourceSystemId()));
        } else {
            cardOrderNew.setCardAthrsgPartyExtSysId(CARD_ATHRSG_PARTY_EXT_SYSID);
        }
        cardOrderNew.setCardAthrsgExtPartyIdTx(primaryInvolvedParty.getPartyIdentifier());
        cardOrderNew.setCardAthrsgPartyTypeCd(CMAS_CARD_ATH_PARTY_TYP);
        cardOrderNew.setCardHoldingPartyId(Long.valueOf(primaryInvolvedParty.getCustomerIdentifier()));
        cardOrderNew.setCardholderNm(cardHolderName);
        cardOrderNew.setCardClassificationCd(CMAS_CARD_ATH_PARTY_TYP);
        CardOrderAccount cardOrderAccount = new CardOrderAccount();
        cardOrderAccount.setSortCd(sortCode);
        cardOrderAccount.setAccountNo8(accountNumber);
        cardOrderAccount.setProdExtSysId(systemCode);
        cardOrderAccount.setExtProdIdTx(productIdentifier);
        cardOrderNew.setCardOrderAccount(cardOrderAccount);
        cardOrderNew.setCardTypeCd(plasticType.getCardTypeCd());
        cardOrderNew.setPlasticTypeCd(plasticType.getPlasticTypeCd());
        cardOrderNew.setNPFeeCollectionRequiredIn(plasticType.getNPFeeCollectionRequiredIn());
        cardOrderNew.setImageRequiredIn(plasticType.getImageRequiredIn());
        return cardOrderNew;
    }

    private String getCCAApplicableIndicator(C808Resp c808Resp) {
        String cCAApplicableIndicator = null;
        if (c808Resp != null && c808Resp.getCardOrderNewAccount() != null && c808Resp.getCardOrderNewAccount().getCardOrderCBSCCA() != null && c808Resp.getCardOrderNewAccount().getCardOrderCBSCCA().getCCAApplicableIn() != null) {
            cCAApplicableIndicator = c808Resp.getCardOrderNewAccount().getCardOrderCBSCCA().getCCAApplicableIn().toString();
        }
        return cCAApplicableIndicator;
    }

    private String getC808ResDecisionText(C808Resp c808Resp) {
        String decisionText = null;
        if (c808Resp != null && c808Resp.getCardAuthoriserNew() != null && c808Resp.getCardAuthoriserNew().getCardOrderCBSDecision() != null && c808Resp.getCardAuthoriserNew().getCardOrderCBSDecision().getCustomerDecisionTypeCd() != null) {
            decisionText = c808Resp.getCardAuthoriserNew().getCardOrderCBSDecision().getCustomerDecisionTypeCd().toString();
        }
        return decisionText;
    }

    private int getC808DebitCardRenewalCode(C808Resp c808Resp) {
        int debitCardRenewalCode = 0;
        if (c808Resp != null && c808Resp.getCardAuthoriserNew() != null && c808Resp.getCardAuthoriserNew().getCardOrderCBSDecision() != null && c808Resp.getCardAuthoriserNew().getCardOrderCBSDecision().getDebitCardRenewalCd() != null) {
            debitCardRenewalCode = c808Resp.getCardAuthoriserNew().getCardOrderCBSDecision().getDebitCardRenewalCd();
        }
        return debitCardRenewalCode;
    }

    private Integer getC808ResDecisionCode(C808Resp c808Resp) {
        Integer decisionCode = null;
        if (c808Resp != null && c808Resp.getCardAuthoriserNew() != null && c808Resp.getCardAuthoriserNew().getCardOrderCBSDecision() != null && c808Resp.getCardAuthoriserNew().getCardOrderCBSDecision().getCustomerDecisionValueCd() != null) {
            decisionCode = c808Resp.getCardAuthoriserNew().getCardOrderCBSDecision().getCustomerDecisionValueCd();
        }
        return decisionCode;
    }

}
