package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.downstream.ocis.client.f336.F336Client;
import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.ReasonText;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Req;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.F336Resp;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.ProductPartyData;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ProductHoldingRetriever {
    private static final Logger LOGGER = Logger.getLogger(ProductHoldingRetriever.class);

    @Autowired
    F336Client f336Client;

    @Autowired
    ExceptionUtility exceptionUtility;

    @Autowired
    HeaderRetriever headerRetriever;

    private static final short EXTERNAL_SYS_ID = 19;

    private static final String CLOSURE_PERIOD_MONTHS_DR = "000";

    private static final String CLOSED_ONLY_IN = "0";

    private static final String CUSTOMER_CONSENT_IN = "1";

    public List<ProductPartyData> getProductHoldings(RequestHeader header) throws SalsaInternalServiceException {

        F336Req request = createF336Request(header);
        F336Resp response = retrieveF336Response(request, header);
        List<ProductPartyData> productPartyDataList = new ArrayList<>();
        productPartyDataList.addAll(response.getProductPartyData());

        while (response.getAdditionalDataIn() == 1) {
            if (response.getSMMTokenTx() != null) {
                request.setSMMTokenTx(response.getSMMTokenTx());
            }
            response = retrieveF336Response(request, header);
            productPartyDataList.addAll(response.getProductPartyData());
        }
        LOGGER.info("Size of ProductPartyData list returned by OCIS F336 retrieveF336Response: " + productPartyDataList.size());
        return productPartyDataList;

    }

    private F336Resp retrieveF336Response(F336Req request, RequestHeader header) throws SalsaInternalServiceException {
        F336Resp response;
        try {
            LOGGER.info("Calling OCIS F336 retrieveF336Response for ExtPartyIdTx: " + request.getExtPartyIdTx());
            ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
            ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
            SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
            response = f336Client.getProductHoldings(request, contactPoint, serviceRequest, securityHeaderType);
        }
        catch (Exception e) {
            String message = "Exception occurred while calling OCIS F336. Returning InternalServiceError ;";
            LOGGER.error(message, e);
            throw new SalsaInternalServiceException(message, null, new ReasonText(e.getMessage()));
        }
        return response;

    }

    private F336Req createF336Request(RequestHeader header) {

        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header.getLloydsHeaders());
        String host = null;
        String partyId = null;
        if (null != bapiInformation && null != bapiInformation.getBAPIHeader() && null != bapiInformation.getBAPIHeader().getStpartyObo()) {
            host = bapiInformation.getBAPIHeader().getStpartyObo().getHost();
            partyId = bapiInformation.getBAPIHeader().getStpartyObo().getPartyid();
        }
        F336Req request = new F336Req();
        request.setExtSysId(EXTERNAL_SYS_ID);
        request.setExtPartyIdTx(partyId);
        request.setPartyId(0l);
        request.setPartyExtSysId((null != host && host.endsWith("L")) ? (short) 1 : (short) 2);
        request.setClosurePeriodMonthsDr(CLOSURE_PERIOD_MONTHS_DR);
        request.setClosedOnlyIn(CLOSED_ONLY_IN);
        request.setCustomerConsentIn(CUSTOMER_CONSENT_IN);

        return request;

    }

}
