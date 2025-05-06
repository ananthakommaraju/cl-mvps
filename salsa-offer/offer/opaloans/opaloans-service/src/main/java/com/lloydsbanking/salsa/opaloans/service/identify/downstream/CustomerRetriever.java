package com.lloydsbanking.salsa.opaloans.service.identify.downstream;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.ocis.client.c216.C216Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.createinvolvedparty.errorcode.OcisErrorCodes;
import com.lloydsbanking.salsa.offer.createinvolvedparty.errorcode.RetrieveOcisErrorMap;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.soap.ocis.c216.objects.C216Req;
import com.lloydsbanking.salsa.soap.ocis.c216.objects.C216Resp;
import com.lloydsbanking.salsa.soap.ocis.c216.objects.PartyProdDataType;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.IndividualName;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class CustomerRetriever {
    private static final Logger LOGGER = Logger.getLogger(CustomerRetriever.class);

    private static final String EXTERNAL_BUSINESS_ERROR = "External Business Error";

    private static final String EXTERNAL_SERVICE_ERROR = "External Service Error";

    private static final short MAX_REPEAT_GROUP_QY = 0;

    private static final short PROD_HELD_EXT_SYS_ID = 4;

    private static final int SALSA_EXT_SYS_ID = 19;

    private static final String APPENDED_ZEROES_FOR_EXT_PROD_HELD_ID_TX = "00000";

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    C216Client c216Client;

    @Autowired
    ExceptionUtility exceptionUtility;

    @Autowired
    RetrieveOcisErrorMap ocisErrorMap;

    public List<Customer> retrieveCustomer(RequestHeader header, String sortCode, String accountNumber) throws ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, ExternalServiceErrorMsg {
        C216Req c216Req = createC216Request(sortCode, accountNumber);
        C216Resp c216Resp = retrieveC216Response(c216Req, header);
        if (isError(c216Resp)) {
            throwErrorInResponse(c216Resp.getC216Result().getResultCondition().getReasonCode(), c216Resp.getC216Result().getResultCondition().getReasonText());
        }

        return getCustomer(c216Resp.getPartyProdData());
    }

    private List<Customer> getCustomer(final List<PartyProdDataType> partyProdDataTypeList) {
        List<Customer> customerList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(partyProdDataTypeList)) {
            for (PartyProdDataType partyProdDataType : partyProdDataTypeList) {
                Customer customer = new Customer();
                customer.setCustomerIdentifier(String.valueOf(partyProdDataType.getPartyId()));
                customer.setCidPersID(partyProdDataType.getExtPartyIdTx());
                customer.setIsPlayedBy(new Individual());
                customer.getIsPlayedBy().setBirthDate(!StringUtils.isEmpty(partyProdDataType.getBirthDt()) ? getBirthDate(partyProdDataType.getBirthDt()) : null);
                customer.getIsPlayedBy().getIndividualName().add(0, new IndividualName());
                customer.getIsPlayedBy().getIndividualName().get(0).setFirstName(partyProdDataType.getFirstForeNm());
                customer.getIsPlayedBy().getIndividualName().get(0).setLastName(partyProdDataType.getSurname());
                customer.getIsPlayedBy().getIndividualName().get(0).setPrefixTitle(partyProdDataType.getPartyTl());
                customerList.add(customer);
            }
        }
        return customerList;
    }

    private XMLGregorianCalendar getBirthDate(final String birthDt) {
        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        XMLGregorianCalendar birthDate = dateFactory.stringToXMLGregorianCalendar(birthDt, formatter);
        if (null != birthDate) {
            birthDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        }
        return birthDate;
    }

    private void throwErrorInResponse(Integer reasonCode, String reasonText) throws ExternalBusinessErrorMsg, ExternalServiceErrorMsg {
        if (OcisErrorCodes.SEVERE_ERROR_OCCURED_CODE.getOcisErrorCode() == reasonCode) {
            LOGGER.error(EXTERNAL_SERVICE_ERROR + " while creating Ocis record via C216 . ErrorNo | ErrorMsg ; " + reasonCode + " | " + reasonText);
            throw exceptionUtility.externalServiceError(ocisErrorMap.getOcisErrorCode(reasonCode), reasonCode + ":" + reasonText);
        } else if (OcisErrorCodes.EXTERNAL_SYSTEM_ID_NOT_PRESENT_CODE.getOcisErrorCode() == reasonCode
                || OcisErrorCodes.UNKNOWN_EXTERNAL_PARTY_ID_CODE.getOcisErrorCode() == reasonCode
                || OcisErrorCodes.UNKNOWN_PRODUCT_HELD_ID.getOcisErrorCode() == reasonCode) {
            LOGGER.error(EXTERNAL_BUSINESS_ERROR + " while creating Ocis record via C216 . ErrorNo | ErrorMsg ; " + reasonCode + " | " + reasonText);
            throw exceptionUtility.externalBusinessError(ocisErrorMap.getOcisErrorCode(reasonCode), reasonCode + ":" + reasonText);
        }
    }

    private C216Resp retrieveC216Response(C216Req c216Req, RequestHeader header) throws ResourceNotAvailableErrorMsg {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header);
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header);
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header);
        try {
            return c216Client.c216(c216Req, contactPoint, serviceRequest, securityHeaderType);
        } catch (WebServiceException webServiceException) {
            LOGGER.error("Exception occurred while calling OCIS C216. Returning ResourceNotAvailableError ;", webServiceException);
            throw exceptionUtility.resourceNotAvailableError(webServiceException.getMessage());
        }
    }

    private C216Req createC216Request(String sortCode, String accountNumber) {
        C216Req c216Req = new C216Req();
        c216Req.setMaxRepeatGroupQy(MAX_REPEAT_GROUP_QY);
        c216Req.setExtSysId(SALSA_EXT_SYS_ID);
        c216Req.setProdHeldExtSysId(PROD_HELD_EXT_SYS_ID);
        c216Req.setExtProdHeldIdTx(sortCode.concat(accountNumber).concat(APPENDED_ZEROES_FOR_EXT_PROD_HELD_ID_TX));
        return c216Req;
    }

    private boolean isError(final C216Resp c216Resp) {
        boolean isValidResultCondition = (null != c216Resp
                && null != c216Resp.getC216Result()
                && null != c216Resp.getC216Result().getResultCondition()
                && null != c216Resp.getC216Result().getResultCondition().getReasonCode());
        return isValidResultCondition && 0 != (c216Resp.getC216Result().getResultCondition().getReasonCode());
    }
}
