package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.downstream.fsystem.client.FsSystemClientImpl;
import com.lloydsbanking.salsa.downstream.fsystem.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.eligibility.service.converter.B093RequestFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.DateUtility;
import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.EligibilityErrorCodes;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.*;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydstsb.ib.wsbridge.system.StB093AEventLogReadList;
import com.lloydstsb.ib.wsbridge.system.StB093BEventLogReadList;
import com.lloydstsb.ib.wsbridge.system.TEventLogReadList;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountEventRetriever {

    private static final Logger LOGGER = Logger.getLogger(AccountEventRetriever.class);

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    B093RequestFactory b093RequestFactory;

    @Autowired
    DateUtility dateUtility;

    @Autowired
    ExceptionUtility exceptionUtility;

    @Autowired
    FsSystemClientImpl fsSystemClient;

    @Autowired
    BapiHeaderToStHeaderConverter bapiHeaderSystemToStHeaderConverter;

    private static final int INTERNAL_SERVICE_ERR_CODE = 131008;

    private static final int EXTERNAL_BUSINESS_ERR_CODE = 131181;

    @Cacheable(value="getAccountEventsCache")
    public List<String> getAccountEvents(String threshold, String eventId, RequestHeader header) throws SalsaInternalServiceException, SalsaExternalBusinessException, SalsaExternalServiceException, SalsaInternalResourceNotAvailableException, DatatypeConfigurationException {
        LOGGER.info("Entering AccountEventRetriever: getAccountEvents threshold: " + threshold + " eventId: " + eventId);
        List<String> accountEventList = new ArrayList<>();
        StB093AEventLogReadList b093Request = createRequest(threshold, eventId, header);
        StB093BEventLogReadList b093Response = retrieveB093Response(b093Request, header);
        if (null != b093Response.getSterror() && b093Response.getSterror().getErrorno() != 0) {
            throwErrorInResponse(b093Response.getSterror().getErrorno(), b093Response.getSterror().getErrormsg(), header);
        }
        for (TEventLogReadList eventLogReadList : b093Response.getAsteventlogreadlist()) {
            accountEventList.add(eventLogReadList.getEvtlogtext());
        }
        LOGGER.info("Entering AccountEventRetriever: getAccountEvents. Size of accountEventList: " + accountEventList.size());
        return accountEventList;
    }

    private void throwErrorInResponse(int errorno, String errormsg, RequestHeader header) throws SalsaInternalServiceException, SalsaExternalBusinessException, SalsaExternalServiceException {
        switch (errorno) {
            case INTERNAL_SERVICE_ERR_CODE:
                LOGGER.error("Internal service error from B093 with reason code : " + errorno + " and Error message :" + errormsg);
                throw new SalsaInternalServiceException(null, EligibilityErrorCodes.ERR_EXTERNAL_SYSTEM_ERROR, new Description(errormsg));
            case EXTERNAL_BUSINESS_ERR_CODE:
                LOGGER.error("External business error from B093 with reason code : " + errorno + " and Error message :" + errormsg);
                throw new SalsaExternalBusinessException(null, String.valueOf(errorno), new ReasonText(errormsg));
            default:
                LOGGER.error("External service error from B093 with reason code : " + errorno + " and Error message :" + errormsg);
                throw new SalsaExternalServiceException(null, String.valueOf(errorno), errormsg);
        }

    }

    private StB093BEventLogReadList retrieveB093Response(StB093AEventLogReadList b093Request, RequestHeader header) throws SalsaInternalResourceNotAvailableException {
        try {
            return fsSystemClient.fetchAccountEvent(b093Request);
        }
        catch (Exception e) {
            LOGGER.error("Exception occurred while calling BAPI B093. Returning ResourceNotAvailableError ;", e);
            throw new SalsaInternalResourceNotAvailableException(e.getMessage());
        }
    }

    private StB093AEventLogReadList createRequest(String threshold, String eventId, RequestHeader header) throws DatatypeConfigurationException, SalsaInternalServiceException {
        try {
            BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header);
            ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header);
            ContactPoint contactPoint = headerRetriever.getContactPoint(header);
            return b093RequestFactory.createB093Request(dateUtility.getStartDate(threshold), dateUtility.getDateInXMLGregorianCalendar(new Date(new Date().getTime())), bapiHeaderSystemToStHeaderConverter
                    .convert(bapiInformation.getBAPIHeader(), serviceRequest, contactPoint.getContactPointId()), bapiInformation.getBAPIHeader().getUseridAuthor(), eventId);
        }
        catch (Exception e) {
            String message = "Exception occurred while creating request for B093. Returning InternalServiceError ;";
            LOGGER.error(message, e);
            throw new SalsaInternalServiceException(message, EligibilityErrorCodes.ERR_EXTERNAL_SYSTEM_ERROR, new ReasonText(e.getMessage()));
        }
    }

}