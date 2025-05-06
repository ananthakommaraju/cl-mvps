package com.lloydsbanking.salsa.ppae.service.process;


import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.apacc.service.fulfil.FulfilPendingCreditCardArrangement;
import com.lloydsbanking.salsa.apapca.service.fulfil.FulfillPendingBankAccountArrangement;
import com.lloydsbanking.salsa.apasa.service.fulfil.FulfilPendingSavingsArrangement;
import com.lloydsbanking.salsa.constant.ArrangementType;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AwaitingFulfilmentApplicationProcessor {
    private static final Logger LOGGER = Logger.getLogger(AwaitingFulfilmentApplicationProcessor.class);

    @Autowired
    FulfillPendingBankAccountArrangement fulfillPendingBankAccountArrangement;
    @Autowired
    FulfilPendingCreditCardArrangement fulfilPendingCreditCardArrangement;
    @Autowired
    FulfilPendingSavingsArrangement fulfilPendingSavingsArrangement;
    @Autowired
    LookUpValueRetriever lookUpValueRetriever;
    @Autowired
    AppGroupRetriever appGroupRetriever;

    private static final String PRODUCT_ACCEPT_CODE = "isAccepted";

    public void process(ProductArrangement productArrangement, RequestHeader header) {
        ActivateProductArrangementRequest activateRequest = createActivateRequest(productArrangement, header);
        ActivateProductArrangementResponse activateResponse = new ActivateProductArrangementResponse();
        try {
            Map<String, Map<String, String>> encryptionKeyAndAccountPurposeMap = lookUpValueRetriever.retrieveEncryptionKeyAndAccountPurposeMap(header);

            switch (ArrangementType.getArrangementType(productArrangement.getArrangementType())) {
                case CURRENT_ACCOUNT:
                    setAppGroupFromSortCodeOrChannel(productArrangement, header);
                    fulfillPendingBankAccountArrangement.fulfillPendingBankAccountArrangement(activateResponse, activateRequest, encryptionKeyAndAccountPurposeMap.get(ActivateCommonConstant.PamGroupCodes.PURPOSE_OF_ACCOUNT_CODE));
                    break;
                case SAVINGS:
                    fulfilPendingSavingsArrangement.fulfilPendingSavingsArrangement(activateResponse, encryptionKeyAndAccountPurposeMap.get(ActivateCommonConstant.PamGroupCodes.PURPOSE_OF_ACCOUNT_CODE), activateRequest);
                    break;
                case CREDITCARD:
                    activateResponse.setProductArrangement(new FinanceServiceArrangement());
                    productArrangement.getAssociatedProduct().getProductoffer().addAll(getAcceptedProductOfferList(productArrangement.getOfferedProducts()));
                    fulfilPendingCreditCardArrangement.fulfilPendingCreditCardArrangement(activateRequest, activateResponse, encryptionKeyAndAccountPurposeMap.get(ActivateCommonConstant.PamGroupCodes.ENCRYPT_KEY_GROUP_CODE));
                    break;
                default:
                    break;
            }

        } catch (ActivateProductArrangementResourceNotAvailableErrorMsg | ActivateProductArrangementExternalSystemErrorMsg | ActivateProductArrangementExternalBusinessErrorMsg | ActivateProductArrangementInternalSystemErrorMsg | ActivateProductArrangementDataNotAvailableErrorMsg e) {
            LOGGER.info("Error while calling activate fulfilment service : " + e);
        }
    }

    private ActivateProductArrangementRequest createActivateRequest(ProductArrangement productArrangement, RequestHeader header) {
        ActivateProductArrangementRequest activateRequest = new ActivateProductArrangementRequest();
        activateRequest.setProductArrangement(productArrangement);
        activateRequest.setHeader(header);
        activateRequest.setSourceSystemIdentifier(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_DB_EVENT_SOURCE_SYSTEM_IDENTIFIER);
        return activateRequest;
    }

    private List<ProductOffer> getAcceptedProductOfferList(List<Product> offeredProductList) {
        List<ProductOffer> productOfferList = new ArrayList<>();
        for (Product product : offeredProductList) {
            if (PRODUCT_ACCEPT_CODE.equalsIgnoreCase(product.getStatusCode())) {
                productOfferList.addAll(product.getProductoffer());
            }
        }
        return productOfferList;
    }

    private void setAppGroupFromSortCodeOrChannel(ProductArrangement productArrangement, RequestHeader header) {
        String channel = null;
        String sortCode = null;
        if (productArrangement.getFinancialInstitution() != null) {
            channel = productArrangement.getFinancialInstitution().getChannel();
            if (!productArrangement.getFinancialInstitution().getHasOrganisationUnits().isEmpty()) {
                sortCode = productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode();
            }
        } else {
            productArrangement.setFinancialInstitution(new Organisation());
        }
        String cbsAppGroup = !StringUtils.isEmpty(sortCode) ? appGroupRetriever.callRetrieveCBSAppGroup(header, sortCode) : channel;
        productArrangement.getFinancialInstitution().setChannel(cbsAppGroup);
    }
}
