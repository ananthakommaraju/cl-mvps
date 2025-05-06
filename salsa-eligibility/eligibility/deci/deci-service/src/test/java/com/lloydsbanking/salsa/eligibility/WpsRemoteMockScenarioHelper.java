package com.lloydsbanking.salsa.eligibility;

import com.lloydsbanking.salsa.downstream.ipa.client.IpaClientImpl;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

public class WpsRemoteMockScenarioHelper extends RemoteMockScenarioHelper {

    @Autowired
    IpaClientImpl ipaClient;

    @Override
    public void expectB162Call(RequestHeader header, String spndngRewardId, String productOneAccountType, String productTwoAccountTyp, String sellerEntity) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        BapiInformation bapiInformation = new BapiInformation();

        ipaClient.retrieveAccessibleArrangements(dataHelper.createRetrieveAccessibleArrangementsRequest(), contactPoint, serviceRequest, securityHeaderType, bapiInformation);

        mockControl.thenReturn(dataHelper.createRetrieveAccessibleArrangementsResponse(productOneAccountType, productTwoAccountTyp,sellerEntity));
    }

}

