package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.downstream.fsystem.client.FsSystemClient;
import com.lloydsbanking.salsa.downstream.fsystem.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.EligibilityErrorCodes;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.ReasonText;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalBusinessException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.wz.EventType;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fs.system.StEventType;
import com.lloydsbanking.salsa.soap.fs.system.StHeader;
import com.lloydstsb.ib.wsbridge.system.StB695AProductEventReadList;
import com.lloydstsb.ib.wsbridge.system.StB695BProductEventReadList;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.List;

public class MandateAccessDetailsRetriever {
    private static final Logger LOGGER = Logger.getLogger(MandateAccessDetailsRetriever.class);

    @Autowired
    FsSystemClient systemClient;

    @Autowired
    BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverter;

    @Autowired
    HeaderRetriever headerRetriever;

    public List<String> getRelatedEvents(RequestHeader header, String accType) throws SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException {
        LOGGER.info("Entering MandateAccessDetailsRetriever with accountType: " + accType);
        List<String> eventList = new ArrayList<>();
        StB695AProductEventReadList b695Request = createB695Request(header, accType);
        StB695BProductEventReadList b695Response = retrieveMandateAccess(b695Request);
        checkForErrorInResponse(b695Response);

        if (null != b695Response && !CollectionUtils.isEmpty(b695Response.getAstevttype())) {
            for (StEventType evtType : b695Response.getAstevttype()) {
                if (null != evtType) {
                    String event = "";
                    try {
                        if (!StringUtils.isEmpty(evtType.getEvttype())) {
                            event = EventType.valueOf(evtType.getEvttype()).asString();
                            eventList.add(event);
                        }
                    }
                    catch (IllegalArgumentException e) {
                        //Consuming exception here because if enum constant is not present than nothing is added in the eventList
                        LOGGER.info("EventType is not present. Therefore, consuming exception" + e);
                    }
                }
            }
        }
        LOGGER.info("Exiting MandateAccessDetailsRetriever with EventList size: " + eventList.size());
        return eventList;
    }

    private void checkForErrorInResponse(final StB695BProductEventReadList b695Response) throws SalsaExternalBusinessException {
        if (null != b695Response && null != b695Response.getSterror() && b695Response.getSterror().getErrorno() != 0) {
            String message = "Returning ExternalBusinessError from B695. " + EligibilityErrorCodes.ERR_EXT_SERVICE_ERROR + b695Response.getSterror().getErrormsg();
            LOGGER.error(message);
            throw new SalsaExternalBusinessException(message, EligibilityErrorCodes.ERR_EXT_SERVICE_ERROR, new ReasonText(b695Response.getSterror().getErrormsg()));
        }
    }

    private StB695BProductEventReadList retrieveMandateAccess(StB695AProductEventReadList request) throws SalsaInternalResourceNotAvailableException {
        try {
            return systemClient.retMandateDetails(request);
        }
        catch (WebServiceException e) {
            String message = "Exception occurred while calling B695. Returning Resource Not available error ";
            LOGGER.error(message, e);
            throw new SalsaInternalResourceNotAvailableException(message, e);
        }
    }

    private StB695AProductEventReadList createB695Request(RequestHeader header, String accType) {
        StB695AProductEventReadList b695Request = new StB695AProductEventReadList();
        BAPIHeader bapiHeader = headerRetriever.getBapiInformationHeader(header).getBAPIHeader();
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header);
        String contactPointId = headerRetriever.getContactPoint(header).getContactPointId();
        StHeader stHeader = bapiHeaderToStHeaderConverter.convert(bapiHeader, serviceRequest, contactPointId);
        b695Request.setStheader(stHeader);
        if (!StringUtils.isEmpty(accType)) {
            b695Request.setAcctype(accType);
        }
        return b695Request;
    }
}
