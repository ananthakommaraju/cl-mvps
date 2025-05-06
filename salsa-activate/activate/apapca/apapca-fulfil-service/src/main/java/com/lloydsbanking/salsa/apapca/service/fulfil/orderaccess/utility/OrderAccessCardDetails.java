package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.utility;


import com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream.C818Factory;
import com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream.FulfilCardOrderRetriever;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.C812Resp;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.CardOrderCBSData;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.CardOrderCBSDecision;
import com.lloydsbanking.salsa.soap.cmas.c818.objects.*;
import com.lloydsbanking.salsa.soap.cmas.c846.objects.C846Resp;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderAccessCardDetails {
    private static final int PIN_SERVICE_CD = 1;

    private static final int POSTAL_ADDRESS_STATUS_CODE = 4;

    private static final String PIN_REQUIRED_IND_0 = "0";

    private static final String PIN_REQUIRED_IND_1 = "1";

    private static final int PIN_CODE_1 = 1;

    private static final int PIN_CODE_2 = 2;

    private static final int PIN_CODE_3 = 3;

    @Autowired
    FulfilCardOrderRetriever fulfilCardOrderRetriever;

    @Autowired
    OrderAccessServiceHelper orderAccessServiceHelper;

    public CardDeliveryAddress getCardDeliveryAddress(C812Resp c812Resp) {
        CardDeliveryAddress cardDeliveryAddress = new CardDeliveryAddress();
        if (null != c812Resp && null != c812Resp.getCardOrderNewValid() && null != c812Resp.getCardOrderNewValid().getCardNewDelivery()) {
            if (null != c812Resp.getCardOrderNewValid().getCardNewDelivery().getAddressExtSysId() && c812Resp.getCardOrderNewValid().getCardNewDelivery().getAddressExtSysId() == POSTAL_ADDRESS_STATUS_CODE) {
                com.lloydsbanking.salsa.soap.cmas.c812.objects.CardDeliveryAddress cardDeliveryAdd = c812Resp.getCardOrderNewValid().getCardNewDelivery().getCardDeliveryAddress();
                cardDeliveryAddress.setAddressLine1Tx(cardDeliveryAdd.getAddressLine1Tx());
                cardDeliveryAddress.setAddressLine2Tx(cardDeliveryAdd.getAddressLine2Tx());
                cardDeliveryAddress.setAddressLine3Tx(cardDeliveryAdd.getAddressLine3Tx());
                cardDeliveryAddress.setAddressLine4Tx(cardDeliveryAdd.getAddressLine4Tx());
                cardDeliveryAddress.setAddressLine5Tx(cardDeliveryAdd.getAddressLine5Tx());
                cardDeliveryAddress.setAddressLine6Tx(cardDeliveryAdd.getAddressLine6Tx());
                cardDeliveryAddress.setAddressLine7Tx(cardDeliveryAdd.getAddressLine7Tx());
                cardDeliveryAddress.setPostCd(cardDeliveryAdd.getPostCd());
            }
        }
        return cardDeliveryAddress;
    }

    public C818Resp fullFillCardOrder(final String sortCode, final String accountNumber, final Customer primaryInvolvedParty, final RequestHeader header, final String cCAApplicableIndicator, final String productIdentifier, final int systemCode, final C846Resp c846Resp, final String cardHolderName, final C812Resp c812Resp, final int cardOrderStatusCd) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        String plasticTypeServiceLevelCd = c812Resp.getCardOrderNewValid().getPlasticTypeServiceLevelCd();
        int pinRequiredIn = c846Resp.getPlasticTypes().getPlasticType().get(0).getPINServiceCd();
        CardOrderAdd cardOrderAdd = orderAccessServiceHelper.getCardOrderAdd(sortCode, accountNumber, primaryInvolvedParty, productIdentifier, systemCode, cardHolderName, c846Resp.getPlasticTypes().getPlasticType().get(0), plasticTypeServiceLevelCd, cardOrderStatusCd);
        CardOrderAddNew cardOrderAddNew = getCardOrderAddNew(pinRequiredIn);
        CardOrderCBSCCA cardOrderCBSCCA = getCardOrderCBSCCA(cCAApplicableIndicator);
        CardDeliveryAddress cardDeliveryAddress = getCardDeliveryAddress(c812Resp);
        CardOrderActions cardOrderAction = new CardOrderActions();
        if (!c812Resp.getCardOrderReferralReasons().getCardOrderReferralReason().isEmpty()) {
            cardOrderAction = orderAccessServiceHelper.getCardOrderActions(cardOrderStatusCd, c812Resp.getCardOrderReferralReasons());
        }

        C818Req c818Request = C818Factory.createC818Request(cardOrderAdd, cardOrderAddNew, cardOrderCBSCCA, cardDeliveryAddress, cardOrderAction);

        return fulfilCardOrderRetriever.getResponse(c818Request, header);
    }

    private CardOrderAddNew getCardOrderAddNew(int pinRequiredIn) {
        CardOrderAddNew cardOrderAddNew = new CardOrderAddNew();

        if (pinRequiredIn == PIN_CODE_1 || pinRequiredIn == PIN_CODE_2) {
            cardOrderAddNew.setPINRequiredIn(String.valueOf(PIN_REQUIRED_IND_1));
        } else if (pinRequiredIn == PIN_CODE_3) {
            cardOrderAddNew.setPINRequiredIn(PIN_REQUIRED_IND_0);
        }

        cardOrderAddNew.setPINServiceCd(PIN_SERVICE_CD);
        return cardOrderAddNew;
    }

    private CardOrderCBSCCA getCardOrderCBSCCA(String cCAApplicableIndicator) {
        CardOrderCBSCCA cardOrderCBSCCA = new CardOrderCBSCCA();
        cardOrderCBSCCA.setCCAApplicableIn(cCAApplicableIndicator);
        return cardOrderCBSCCA;
    }

    public CardOrderCBSData getCardOrderCBSData(int debitCardRenewalCode, String cCAApplicableIndicator, String decisionText, Integer decisionCode) {
        CardOrderCBSData cardOrderCBSData = new CardOrderCBSData();
        CardOrderCBSDecision cardOrderCBSDecision = new CardOrderCBSDecision();
        cardOrderCBSDecision.setCustomerDecisionTypeCd(decisionText);
        if (null != decisionCode) {
            cardOrderCBSDecision.setCustomerDecisionValueCd(decisionCode);
        }
        cardOrderCBSDecision.setDebitCardRenewalCd(debitCardRenewalCode);
        cardOrderCBSData.setCCAApplicableIn(cCAApplicableIndicator);
        cardOrderCBSData.setCardOrderCBSDecision(cardOrderCBSDecision);
        return cardOrderCBSData;
    }

}
