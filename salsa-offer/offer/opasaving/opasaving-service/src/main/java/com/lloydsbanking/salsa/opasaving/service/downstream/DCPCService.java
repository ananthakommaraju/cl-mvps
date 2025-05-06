package com.lloydsbanking.salsa.opasaving.service.downstream;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.xml.schema.enterprise.lcsm5.services.definitions.RequestGroup;
import com.lloydsbanking.xml.schema.enterprise.lcsm5.services.productcustomermatching.v2.productcustomermatching.serviceparameters.iproductcustomermatching.DetermineCustomerProductConditionsRequest;
import com.lloydsbanking.xml.schema.enterprise.lcsm5.services.productcustomermatching.v2.productcustomermatching.serviceparameters.iproductcustomermatching.DetermineCustomerProductConditionsResponse;
import com.lloydsbanking.xml.schema.enterprise.lcsm5.transferobjects.productcustomermatching.v2.PCMCategory;
import com.lloydsbanking.xml.schema.enterprise.lcsm5.transferobjects.productcustomermatching.v2.PCMCustomer;
import com.lloydsbanking.xml.schema.enterprise.lcsm5.transferobjects.productcustomermatching.v2.PCMProduct;
import com.lloydsbanking.xml.schema.enterprise.lcsm5.transferobjects.productcustomermatching.v2.PCMTariff;
import com.lloydsbanking.xml.schema.enterprise.structuralcomponents.InvolvedPartyIdentifier;
import com.lloydsbanking.xml.schema.enterprise.structuralcomponents.ProductIdentifier;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import java.math.BigInteger;
import java.util.List;

public class DCPCService {

    private static final Logger LOGGER = Logger.getLogger(DCPCService.class);
    private static final String IB_SYSTEM_CODE = "00004";
    private static final String TARIFF = "Tariff";
    private static final int PRODUCT_IDENTIFIER_LENGTH = 4;
    private static final String PRIMARY_CATEGORYID = "PRIMARY_CATEGORYID";
    private static final String PRIMARY_CUSTOMER = "PRIMARY_CUSTOMER";
    private static final int REQUEST_GROUP_QUANTITY = 1;
    private static final String EXTERNAL_APPLICATION_ID = "AL02760";

    @Autowired
    DCPCServiceClient dcpcServiceClient;

    public void callDCPCService(RetrieveProductConditionsResponse rpcResp, DepositArrangement requestDepositArrangement, RequestHeader requestHeader) throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        DetermineCustomerProductConditionsRequest dcpcRequest = convertOfferToDcpcRequest(rpcResp, requestDepositArrangement, requestHeader);
        LOGGER.info("Entering into determineCustomerProductCondition, CustomerIdentifier | ProductIdentifier; " + dcpcRequest.getPartyDetails().get(0).getCustomerIdentifiers().getIdentifier() + " | " + dcpcRequest.getProductDetails().get(0).getIdentifier().getIdentifier());

        DetermineCustomerProductConditionsResponse dcpcResponse = dcpcServiceClient.retrieveDCPCResponse(dcpcRequest, requestHeader);
        if (null != dcpcResponse && !CollectionUtils.isEmpty(dcpcResponse.getProductConditions()) && !CollectionUtils.isEmpty(dcpcResponse.getProductConditions().get(0).getTariffs()) && !CollectionUtils.isEmpty(dcpcResponse.getProductConditions().get(0).getTariffs().get(0).getPreferentialRates())) {
            if (CollectionUtils.isEmpty(requestDepositArrangement.getAssociatedProduct().getProductPreferentialRate())) {
                requestDepositArrangement.getAssociatedProduct().getProductPreferentialRate().add(new PreferentialRate());
            }
            requestDepositArrangement.getAssociatedProduct().getProductPreferentialRate().get(0).setPreferentialRateIdentifier(dcpcResponse.getProductConditions().get(0).getTariffs().get(0).getPreferentialRates().get(0).getPreferentialRateIdentifier());
            LOGGER.info("PPR_ID from DetermineCustomerProductCondition: " + requestDepositArrangement.getAssociatedProduct().getProductPreferentialRate().get(0).getPreferentialRateIdentifier());
        }
        LOGGER.info("Exiting DetermineCustomerProductCondition");
    }

    private DetermineCustomerProductConditionsRequest convertOfferToDcpcRequest(RetrieveProductConditionsResponse rpcResp, DepositArrangement requestDepositArrangement, RequestHeader requestHeader) throws InternalServiceErrorMsg {
        DetermineCustomerProductConditionsRequest dcpcRequest = new DetermineCustomerProductConditionsRequest();
        dcpcRequest.setRequestHeader(new com.lloydsbanking.xml.schema.enterprise.lcsm5.services.definitions.RequestHeader());
        dcpcRequest.getRequestHeader().setRequestGroupQuantity(BigInteger.valueOf(REQUEST_GROUP_QUANTITY));
        dcpcRequest.getRequestHeader().getRequestGroup().add(new RequestGroup());
        dcpcRequest.getRequestHeader().getRequestGroup().get(0).setExternalApplicationId(EXTERNAL_APPLICATION_ID);
        dcpcRequest.getPartyDetails().add(new PCMCustomer());
        dcpcRequest.getPartyDetails().get(0).getCategories().add(new PCMCategory());
        dcpcRequest.getPartyDetails().get(0).getCategories().get(0).setIdentifier("Y");
        dcpcRequest.getPartyDetails().get(0).getCategories().get(0).setDescription(PRIMARY_CATEGORYID);
        dcpcRequest.getPartyDetails().get(0).getConsentShareValues().add("y");
        dcpcRequest.getPartyDetails().get(0).setCustomerCategoryRequired(true);
        try {
            dcpcRequest.getPartyDetails().get(0).setAuditDate(new DateFactory().dateToXMLGregorianCalendar(new DateFactory().currentDateTime()));
        } catch (DatatypeConfigurationException e) {
            LOGGER.info("Offer: Identify: Exception caught while date conversion: ", e);
        }
        dcpcRequest.getPartyDetails().get(0).setCustomerIdentifiers(new InvolvedPartyIdentifier());
        dcpcRequest.getPartyDetails().get(0).getCustomerIdentifiers().setIdentifier((requestDepositArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()));
        dcpcRequest.getPartyDetails().get(0).setDescription(PRIMARY_CUSTOMER);
        dcpcRequest.getProductDetails().add(new PCMProduct());
        dcpcRequest.getProductDetails().get(0).setIdentifier(new ProductIdentifier());
        if (null != rpcResp && !CollectionUtils.isEmpty(rpcResp.getProduct())) {
            LOGGER.info("Product is not null, checking externalSystemProductIdentifier");
            Product productFamilyMember = rpcResp.getProduct().get(0);
            List<ExtSysProdIdentifier> extSysProdIdentifierList = productFamilyMember.getExternalSystemProductIdentifier();
            if (!CollectionUtils.isEmpty(extSysProdIdentifierList)) {
                for (ExtSysProdIdentifier extSysProdIdentifier : extSysProdIdentifierList) {
                    if (null != extSysProdIdentifier.getSystemCode() && extSysProdIdentifier.getSystemCode().equalsIgnoreCase(IB_SYSTEM_CODE) && null != extSysProdIdentifier.getProductIdentifier()) {
                        dcpcRequest.getProductDetails().get(0).getIdentifier().setIdentifier((extSysProdIdentifier.getProductIdentifier().substring(0, PRODUCT_IDENTIFIER_LENGTH)));
                    }
                }
            }
            dcpcRequest.getProductDetails().get(0).getTariffs().add(new PCMTariff());
            dcpcRequest.getProductDetails().get(0).getTariffs().get(0).setIdentifier(getFeatureValue(productFamilyMember));
        }
        dcpcRequest.getProductDetails().get(0).setBrandName(requestHeader.getChannelId());
        return dcpcRequest;
    }

    private String getFeatureValue(Product productFamilyMember) {
        String featureValue = "";
        if (!CollectionUtils.isEmpty(productFamilyMember.getProductoptions())) {
            for (ProductOptions feature : productFamilyMember.getProductoptions()) {
                if (null != feature.getOptionsName() && feature.getOptionsName().contains(TARIFF) && !StringUtils.isEmpty(feature.getOptionsValue())) {
                    featureValue = feature.getOptionsValue();
                    LOGGER.info("Feature value " + featureValue);
                }
            }
        }
        return featureValue;
    }
}
