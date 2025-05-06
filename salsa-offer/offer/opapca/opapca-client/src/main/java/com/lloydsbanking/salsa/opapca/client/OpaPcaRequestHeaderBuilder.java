package com.lloydsbanking.salsa.opapca.client;

import com.lloydstsb.schema.enterprise.lcsm.*;
import com.lloydstsb.schema.infrastructure.OperatorTypeEnum;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_gmo.messages.SOAPHeader;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameToken;

public class OpaPcaRequestHeaderBuilder {
    public static final String BAPI_HOST_PARTY_ID = "+00434307833";

    public static final String IP_CALLER_ID = "10.245.218.11";

    RequestHeader requestHeader;

    public OpaPcaRequestHeaderBuilder() {
        this.requestHeader = new RequestHeader();
    }

    public OpaPcaRequestHeaderBuilder interactionId(String interactionId) {
        requestHeader.setInteractionId(interactionId);
        return this;
    }

    public OpaPcaRequestHeaderBuilder channelId(String channelId) {
        requestHeader.setChannelId(channelId);
        return this;
    }

    public OpaPcaRequestHeaderBuilder businessTransaction(String businessTransaction) {
        requestHeader.setBusinessTransaction(businessTransaction);
        return this;
    }

    public OpaPcaRequestHeaderBuilder bapiInformation(String chanId, String interactionId, String ocisId, String prefix, String colleagueId, String ucId) {
        BapiInformation bapiInfo = new BapiInformation();
        bapiInfo.setBAPIId("B001");

        OperationalVariables operationalVariables = new OperationalVariables();
        operationalVariables.setBForceHostCall(Boolean.FALSE);
        operationalVariables.setBPopulateCache(Boolean.FALSE);
        operationalVariables.setBBatchRetry(Boolean.FALSE);
        bapiInfo.setBAPIOperationalVariables(operationalVariables);

        BAPIHeader bapiHeader = new BAPIHeader();
        bapiHeader.setUseridAuthor("OX982035");

        HostInformation hostInformation = new HostInformation();
        hostInformation.setHost("T");
        hostInformation.setPartyid(BAPI_HOST_PARTY_ID);
        hostInformation.setOcisid(ocisId);
        bapiHeader.setStpartyObo(hostInformation);

        bapiHeader.setChanid(chanId);
        bapiHeader.setChansecmode("PWD");
        bapiHeader.setSessionid(interactionId);
        bapiHeader.setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.1.7) Gecko/20091221 Firefox/3.5.7");
        bapiHeader.setInboxidClient("GX");

        if (null != colleagueId) {
            ColleagueDetails colleagueDetails = new ColleagueDetails();
            colleagueDetails.setColleagueid(colleagueId);
            bapiHeader.getStcolleaguedetails().add(colleagueDetails);
        }

        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setUcid(ucId);
        bapiHeader.getSttransactiondetails().add(transactionDetails);
        bapiHeader.setCallerlineid("127.0.0.1");
        bapiHeader.setIpAddressCaller("127.0.0.1");
        bapiInfo.setBAPIHeader(bapiHeader);

        SOAPHeader bapiSoapHeader = new SOAPHeader();
        bapiSoapHeader.setValue(bapiInfo);
        bapiSoapHeader.setName("bapiInformation");
        bapiSoapHeader.setPrefix(prefix);
        bapiSoapHeader.setNameSpace("http://www.lloydstsb.com/Schema/Enterprise/LCSM");

        requestHeader.getLloydsHeaders().add(bapiSoapHeader);

        return this;
    }

    public OpaPcaRequestHeaderBuilder securityHeader(String prefix, String username) {
        SecurityHeaderType securityHeader = new SecurityHeaderType();
        UsernameToken usernameToken = new UsernameToken();
        usernameToken.setUsername(username);
        usernameToken.setId("LloydsTSBSecurityToken");
        usernameToken.setUNPMechanismType("NTLM");
        usernameToken.setUserType("013");
        securityHeader.setUsernameToken(usernameToken);

        SOAPHeader securitySoapHeader = new SOAPHeader();
        securitySoapHeader.setValue(securityHeader);
        securitySoapHeader.setName("Security");
        securitySoapHeader.setPrefix(prefix);
        securitySoapHeader.setNameSpace("http://LB_GBO_Sales/Messages");
        requestHeader.getLloydsHeaders().add(securitySoapHeader);

        return this;
    }

    public OpaPcaRequestHeaderBuilder serviceRequest(String prefix, String businessTransaction, String from, String messageId) {
        ServiceRequest serviceRequestHeader = new ServiceRequest();
        serviceRequestHeader.setServiceName("{http://www.lloydstsb.com/Schema/Enterprise/LCSM_CommunicationManagement}CommunicationAcceptanceService");
        serviceRequestHeader.setAction(businessTransaction);
        serviceRequestHeader.setFrom(from);
        serviceRequestHeader.setMessageId(messageId);

        SOAPHeader serviceRequestSoapHeader = new SOAPHeader();
        serviceRequestSoapHeader.setValue(serviceRequestHeader);
        serviceRequestSoapHeader.setName("ServiceRequest");
        serviceRequestSoapHeader.setNameSpace("http://www.lloydstsb.com/Schema/Infrastructure/SOAP");
        serviceRequestSoapHeader.setPrefix(prefix);
        requestHeader.getLloydsHeaders().add(serviceRequestSoapHeader);
        return this;
    }

    public OpaPcaRequestHeaderBuilder contactPoint(String prefix, String contactPointType, String contactPointId, String applicationId, String initialOriginatorType, String initialOriginatorId, String operatorType) {
        ContactPoint contactPointHeader = new ContactPoint();
        contactPointHeader.setContactPointType(contactPointType);
        contactPointHeader.setContactPointId(contactPointId);
        contactPointHeader.setApplicationId(applicationId);
        contactPointHeader.setInitialOriginatorType(initialOriginatorType);
        contactPointHeader.setInitialOriginatorId(initialOriginatorId);
        contactPointHeader.setOperatorType(OperatorTypeEnum.fromValue(operatorType));

        SOAPHeader contactPointSoapHeader = new SOAPHeader();
        contactPointSoapHeader.setValue(contactPointHeader);
        contactPointSoapHeader.setName("ContactPoint");
        contactPointSoapHeader.setNameSpace("http://www.lloydstsb.com/Schema/Infrastructure/SOAP");
        contactPointSoapHeader.setPrefix(prefix);
        requestHeader.getLloydsHeaders().add(contactPointSoapHeader);
        return this;
    }

    public RequestHeader build() {
        return requestHeader;
    }
}
