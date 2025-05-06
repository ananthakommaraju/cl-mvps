package com.lloydsbanking.salsa.offer.identify.downstream;

import com.lloydsbanking.salsa.downstream.ocis.client.f336.F336Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.offer.identify.convert.ProductPartyDataToProductConverter;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Req;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Resp;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.ProductPartyData;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Product;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.List;

public class ProductHoldingRetriever {
    private static final Logger LOGGER = Logger.getLogger(ProductHoldingRetriever.class);
    private static final short EXTERNAL_SYS_ID = 19;
    private static final String CLOSURE_PERIOD_MONTHS_DR = "000";
    private static final String CLOSED_ONLY_IN = "0";
    private static final String CUSTOMER_CONSENT_IN = "2";

    @Autowired(required = false)
    F336Client f336Client;
    @Autowired
    ExceptionUtility exceptionUtility;
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    ProductPartyDataToProductConverter productPartyDataToProductConverter;
    @Autowired
    ProductTraceLog productTraceLog;

    public List<Product> getProductHoldings(RequestHeader header, String partyIdentifier) throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        LOGGER.info("Entering RetrieveProductHoldings (OCIS F336) with Party Identifier: " + partyIdentifier);
        F336Req request = createF336Request(header, partyIdentifier);
        List<ProductPartyData> productPartyDataList = new ArrayList<>();
        int needAdditionalData = 1;

        while (needAdditionalData == 1) {
            F336Resp response = retrieveF336Response(request, header);
            if (response.getSMMTokenTx() != null) {
                request.setSMMTokenTx(response.getSMMTokenTx());

            }
            needAdditionalData = response.getAdditionalDataIn();
            throwErrorForNonZeroSevCode(response);
            productPartyDataList.addAll(response.getProductPartyData());
        }
        List<Product> productList = productPartyDataToProductConverter.convert(productPartyDataList, header.getChannelId());
        if (!CollectionUtils.isEmpty(productList)) {
            productTraceLog.getProdListTraceEventMessage(productList, "Product list from RetrieveProductHoldings: ");
        } else {
            LOGGER.info("Product list is empty from RetrieveProductHoldings");
        }
        LOGGER.info("Exiting RetrieveProductHoldings (OCIS F336)");
        return productList;
    }

    private void throwErrorForNonZeroSevCode(F336Resp response) throws ExternalServiceErrorMsg {
        if (null != response.getF336Result() && null != response.getF336Result().getResultCondition() && 0 != response.getF336Result().getResultCondition().getSeverityCode()) {
            LOGGER.error("Non zero severity code returned from OCIS F336. Returning ExternalServiceError. ErrorCode | ReasonText ; " + response.getF336Result().getResultCondition().getReasonCode() + " | " + response.getF336Result()
                    .getResultCondition()
                    .getReasonText());
            throw exceptionUtility.externalServiceError("823009", (response.getF336Result().getResultCondition().getReasonCode() + ":" + response.getF336Result()
                    .getResultCondition()
                    .getReasonText()));

        }
    }

    private F336Resp retrieveF336Response(F336Req request, RequestHeader header) throws ResourceNotAvailableErrorMsg {
        F336Resp response;
        try {
            ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
            ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
            SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
            response = f336Client.getProductHoldings(request, contactPoint, serviceRequest, securityHeaderType);
        } catch (WebServiceException e) {
            //throwing resourceNotAvailable for exceptions from client
            LOGGER.error("Exception occurred while calling OCIS F336. Returning ResourceNotAvailableError ;", e);
            throw exceptionUtility.resourceNotAvailableError(e.getMessage());
        }
        return response;

    }

    private F336Req createF336Request(RequestHeader header, String partyIdentifier) {
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header.getLloydsHeaders());
        String host = null;
        if (null != bapiInformation && null != bapiInformation.getBAPIHeader() && null != bapiInformation.getBAPIHeader().getStpartyObo()) {
            host = bapiInformation.getBAPIHeader().getStpartyObo().getHost();
        }
        F336Req request = new F336Req();
        request.setExtSysId(EXTERNAL_SYS_ID);
        request.setExtPartyIdTx(partyIdentifier);
        request.setPartyId(null != partyIdentifier ? Long.valueOf(partyIdentifier) : 0l);
        request.setPartyExtSysId((null != host && host.equals("L")) ? (short) 1 : (short) 2);
        request.setClosurePeriodMonthsDr(CLOSURE_PERIOD_MONTHS_DR);
        request.setClosedOnlyIn(CLOSED_ONLY_IN);
        request.setCustomerConsentIn(CUSTOMER_CONSENT_IN);
        return request;

    }

}
