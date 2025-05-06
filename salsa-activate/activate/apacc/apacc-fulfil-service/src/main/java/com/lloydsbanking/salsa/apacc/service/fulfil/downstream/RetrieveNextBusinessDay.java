package com.lloydsbanking.salsa.apacc.service.fulfil.downstream;

import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.fsystem.client.FsSystemClient;
import com.lloydsbanking.salsa.downstream.fsystem.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fs.system.StHeader;
import com.lloydstsb.ib.wsbridge.system.StB748AWrkngDateAfterXDays;
import com.lloydstsb.ib.wsbridge.system.StB748BWrkngDateAfterXDays;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Repository
public class RetrieveNextBusinessDay {
    private static final Logger LOGGER = Logger.getLogger(RetrieveNextBusinessDay.class);

    private static final int NUMBER_OF_DAYS_1 = 1;
    private static final int NUMBER_OF_DAYS_2 = 2;
    private static final int NUMBER_OF_DAYS_3 = 3;
    private static final String USERID_AUTHOR = "AAGATEWAY";
    private static final int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;

    @Autowired
    FsSystemClient fsSystemClient;

    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;

    @Autowired
    BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverterSystem;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    DateFactory dateFactory;

    public StB748BWrkngDateAfterXDays retrieveNextBusinessDay(RequestHeader requestHeader, ApplicationDetails applicationDetails) {
        StB748BWrkngDateAfterXDays stB748BWrkngDateAfterXDays = null;
        try {
            StB748AWrkngDateAfterXDays stB748AWrkngDateAfterXDays = createB748Request(requestHeader);
            LOGGER.info("Entering RetrieveNextBusinesDay (BAPI B748) with NoOfDays |  dateUserRequested: " + stB748AWrkngDateAfterXDays.getNumOfDays() + " | " + stB748AWrkngDateAfterXDays.getDateUserRequested());
            stB748BWrkngDateAfterXDays = invokeB748(requestHeader, stB748AWrkngDateAfterXDays);
            setApplicationDetails(applicationDetails, ApplicationStatus.AWAITING_POST_FULFILMENT_PROCESS.getValue(), null);
            isErrorScenario(stB748BWrkngDateAfterXDays, requestHeader);
        } catch (ActivateProductArrangementResourceNotAvailableErrorMsg | ActivateProductArrangementExternalSystemErrorMsg e) {
            LOGGER.info("Error in B748 while retrieving next business day: This exception will be consumed " + e);
            setApplicationDetails(applicationDetails, ApplicationStatus.AWAITING_POST_FULFILMENT_PROCESS.getValue(), null);
            stB748BWrkngDateAfterXDays = new StB748BWrkngDateAfterXDays();
            stB748BWrkngDateAfterXDays.setDateNextWorking(getNextBusinessDay());
        }
        LOGGER.info("Exiting RetrieveNextBusinesDay (BAPI B748) with nextWorkingDate: " + (stB748BWrkngDateAfterXDays != null ? stB748BWrkngDateAfterXDays.getDateNextWorking() + " " : null));
        return stB748BWrkngDateAfterXDays;
    }

    private void isErrorScenario(StB748BWrkngDateAfterXDays stB748BWrkngDateAfterXDays, RequestHeader requestHeader) throws ActivateProductArrangementExternalSystemErrorMsg {
        if (stB748BWrkngDateAfterXDays != null && stB748BWrkngDateAfterXDays.getSterror() != null && stB748BWrkngDateAfterXDays.getSterror().getErrorno() > 0) {
            LOGGER.error("B748 responded with non zero severity code. Returning ExternalServiceError. ReasonCode | ReasonText " + stB748BWrkngDateAfterXDays.getSterror().getErrorno() + " | " + stB748BWrkngDateAfterXDays.getSterror().getErrormsg());
            throw exceptionUtilityActivate.externalServiceError(requestHeader, stB748BWrkngDateAfterXDays.getSterror().getErrormsg(), String.valueOf(stB748BWrkngDateAfterXDays.getSterror().getErrorno()));
        }
    }

    private StB748AWrkngDateAfterXDays createB748Request(RequestHeader requestHeader) {
        BAPIHeader bapiHeader = headerRetriever.getBapiInformationHeader(requestHeader).getBAPIHeader();
        String contactPointId = headerRetriever.getContactPoint(requestHeader).getContactPointId();
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader);
        StHeader stHeader = bapiHeaderToStHeaderConverterSystem.convert(bapiHeader, serviceRequest, contactPointId);
        stHeader.setStpartyObo(null);
        stHeader.setUseridAuthor(USERID_AUTHOR);
        StB748AWrkngDateAfterXDays stB748AWrkngDateAfterXDays = new StB748AWrkngDateAfterXDays();
        stB748AWrkngDateAfterXDays.setStheader(stHeader);
        stB748AWrkngDateAfterXDays.setNumOfDays(NUMBER_OF_DAYS_1);
        stB748AWrkngDateAfterXDays.setDateUserRequested(dateFactory.getCurrentDate());
        return stB748AWrkngDateAfterXDays;
    }

    private StB748BWrkngDateAfterXDays invokeB748(RequestHeader requestHeader, StB748AWrkngDateAfterXDays stB748AWrkngDateAfterXDays) throws ActivateProductArrangementResourceNotAvailableErrorMsg {
        StB748BWrkngDateAfterXDays stB748BWrkngDateAfterXDays = null;
        try {
            stB748BWrkngDateAfterXDays = fsSystemClient.retrieveNextBusinessDay(stB748AWrkngDateAfterXDays);
        } catch (WebServiceException e) {
            LOGGER.error("Error while calling B748 for retrieve next business day" + e);
            throw exceptionUtilityActivate.resourceNotAvailableError(requestHeader, e.getMessage());
        }
        return stB748BWrkngDateAfterXDays;
    }

    private void setApplicationDetails(ApplicationDetails applicationDetails, String appStatus, String appSubStatus) {
        applicationDetails.setApplicationStatus(appStatus);
        applicationDetails.setApplicationSubStatus(appSubStatus);
    }

    public XMLGregorianCalendar getNextBusinessDay() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String nextDate = dateFormat.format(date.getTime() + MILLIS_IN_DAY);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            nextDate = dateFormat.format(date.getTime() + NUMBER_OF_DAYS_3 * MILLIS_IN_DAY);
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            nextDate = dateFormat.format(date.getTime() + NUMBER_OF_DAYS_2 * MILLIS_IN_DAY);
        }
        return dateFactory.stringToXMLGregorianCalendar(nextDate, new SimpleDateFormat("yyyy/MM/dd"));
    }
}
