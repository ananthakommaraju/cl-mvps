package com.lloydsbanking.salsa.eligibility.wz;

import com.lloydsbanking.salsa.downstream.ipa.client.IpaClient;
import com.lloydsbanking.salsa.remotemock.MockControlServicePortType;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.ErrorInfo;
import com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.RetrieveMandateAccessDetailsRequest;
import com.lloydstsb.schema.enterprise.lcsm_involvedpartymanagement.RetrieveMandateAccessDetailsResponse;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

public class WpsRemoteMockScenarioHelper extends RemoteMockScenarioHelper {
    @Autowired
    IpaClient ipaClient;

    @Autowired
    MockControlServicePortType mockControl;

    @Override
    public void expectB695Call(RequestHeader header, String accType, String eventType) throws ErrorInfo {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        BapiInformation bapiInformation = new BapiInformation();
        RetrieveMandateAccessDetailsRequest request = dataHelper.createRetrieveMandateAccessDetailsRequest(accType);

        RetrieveMandateAccessDetailsResponse response = dataHelper.createRetrieveMandateAccessDetailsResponse(eventType);
        ipaClient.retrieveMandateAccessDetails(request, contactPoint, serviceRequest, securityHeaderType);
        mockControl.thenReturn(response);
    }
}
