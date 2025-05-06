package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.utility;

import com.lloydsbanking.salsa.soap.cmas.c812.objects.CardOrderReferralReason;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.CardOrderReferralReasons;
import com.lloydsbanking.salsa.soap.cmas.c818.objects.CardOrderAction;
import com.lloydsbanking.salsa.soap.cmas.c818.objects.CardOrderActions;
import com.lloydsbanking.salsa.soap.cmas.c818.objects.CardOrderAdd;
import com.lloydsbanking.salsa.soap.cmas.c846.objects.PlasticType;
import lib_sim_bo.businessobjects.Customer;
import org.springframework.stereotype.Component;

@Component
public class OrderAccessServiceHelper {
    private static final int CMAS_ACTION_TYPE_CODE_APPROVE = 0;

    private static final int CARD_ORDER_STATUS_CODE_APPROVED = 1;

    private static final int CMAS_CODE_NEW_CARD_ORDER = 5;

    private static final String CARD_ORDER_AUTHORITY_CODE_Y = "Y";

    private static final String CARD_ORDER_CONSENT_CD_A = "A";

    private static final int CARD_ORDER_TYPE_CODE_ONE = 1;

    private static final int CMAS_ACTION_CODE_UNDEFINED = 0;

    private static final int CMAS_ACTION_CODE_AVAILABLE = 1;

    private static final int CMAS_ACTION_CODE_OVERRIDE = 4;

    private static final int CMAS_ACTION_CODE_REFER = 5;

    private static final int CMAS_ACTION_CODE_APPROVE = 6;

    private static final int CARD_ORDER_STATUS_DS_CODE_ZERO = 0;

    private static final int CUSTOMER_COLLECT_NOT_OFFERED = 0;

    private static final String CHEQUE_BOOK_ORDERED_IN = "0";

    private static final String CARD_CLASSIFICATION_CD = "P";

    public CardOrderAdd getCardOrderAdd(String sortCode, String accountNumber, Customer primaryInvolvedParty, String productIdentifier, int systemCode, String cardHolderName, PlasticType plasticType, String plasticTypeServiceLevelCd, int cardOrderStatus) {
        CardOrderAdd cardOrderAdd = new CardOrderAdd();
        cardOrderAdd.setCardAuthorisingPartyId(Long.valueOf(primaryInvolvedParty.getCustomerIdentifier()));
        cardOrderAdd.setCardHoldingPartyId(Long.valueOf(primaryInvolvedParty.getCustomerIdentifier()));
        com.lloydsbanking.salsa.soap.cmas.c818.objects.CardOrderAccount cardOrderAccount = new com.lloydsbanking.salsa.soap.cmas.c818.objects.CardOrderAccount();
        cardOrderAccount.setSortCd(sortCode);
        cardOrderAccount.setAccountNo8(accountNumber);
        cardOrderAccount.setProdExtSysId(systemCode);
        cardOrderAccount.setExtProdIdTx(productIdentifier);
        cardOrderAdd.setCardOrderAccount(cardOrderAccount);
        cardOrderAdd.setCardOrderStatusCd(cardOrderStatus);
        cardOrderAdd.setCardOrderStatusDsCd(CARD_ORDER_STATUS_DS_CODE_ZERO);
        cardOrderAdd.setCardholderNm(cardHolderName);
        cardOrderAdd.setCardOrderAuthorityCd(CARD_ORDER_AUTHORITY_CODE_Y);
        cardOrderAdd.setCardOrderConsentCd(CARD_ORDER_CONSENT_CD_A);
        cardOrderAdd.setCardOrderTypeCd(CARD_ORDER_TYPE_CODE_ONE);
        cardOrderAdd.setCardTypeCd(plasticType.getCardTypeCd());
        cardOrderAdd.setPlasticTypeCd(plasticType.getPlasticTypeCd());
        cardOrderAdd.setCustomerCollectIn(CUSTOMER_COLLECT_NOT_OFFERED);
        cardOrderAdd.setChequeBookOrderedIn(CHEQUE_BOOK_ORDERED_IN);
        cardOrderAdd.setCardClassificationCd(CARD_CLASSIFICATION_CD);
        cardOrderAdd.setPlasticTypeServiceLevelCd(plasticTypeServiceLevelCd);
        return cardOrderAdd;
    }

    public CardOrderActions getCardOrderActions(int cardOrderStatusCd, CardOrderReferralReasons cardOrderReferralReasons) {

        CardOrderActions cardOrderActions = new CardOrderActions();
        for (CardOrderReferralReason cardOrderReferralReason : cardOrderReferralReasons.getCardOrderReferralReason()) {
            CardOrderAction cardOrderAction = new CardOrderAction();
            Long cMASActionCd = cardOrderReferralReason.getCardOrderReferralReasonCd();
            cardOrderAction.setCMASActionCd(getCMASActionTypeCode(cardOrderReferralReason.getCardOrderReferralReasonCd()));
            cardOrderAction.setCMASActionTypeCd(cMASActionCd.intValue());
            cardOrderAction.setCMASFunctionCd(CMAS_CODE_NEW_CARD_ORDER);
            cardOrderActions.getCardOrderAction().add(cardOrderAction);
        }
        CardOrderAction cardOrderAction1 = new CardOrderAction();
        if (cardOrderStatusCd == CARD_ORDER_STATUS_CODE_APPROVED) {
            cardOrderAction1.setCMASActionCd(CMAS_ACTION_CODE_APPROVE);
        }
        else {
            cardOrderAction1.setCMASActionCd(CMAS_ACTION_CODE_REFER);
        }
        cardOrderAction1.setCMASActionTypeCd(CMAS_ACTION_TYPE_CODE_APPROVE);
        cardOrderAction1.setCMASFunctionCd(CMAS_CODE_NEW_CARD_ORDER);
        cardOrderActions.getCardOrderAction().add(cardOrderAction1);
        return cardOrderActions;

    }

    public int getCMASActionTypeCode(long cmasAction) {
        if (CMASReferralReasons.AUTH_RECEIVED.equalsCode(cmasAction)) {
            return CMAS_ACTION_CODE_AVAILABLE;
        }
        else if (CMASReferralReasons.AGREEMENT_AWAITED.equalsCode(cmasAction)) {
            return CMAS_ACTION_CODE_OVERRIDE;
        }
        return CMAS_ACTION_CODE_UNDEFINED;
    }



}
