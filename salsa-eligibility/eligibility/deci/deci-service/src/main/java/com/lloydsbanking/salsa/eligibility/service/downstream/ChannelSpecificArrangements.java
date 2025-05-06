package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.downstream.user.client.UserClient;
import com.lloydsbanking.salsa.downstream.user.convert.BapiHeaderUserToStHeaderConverter;
import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.ReasonText;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fs.user.StAccountListDetail;
import com.lloydstsb.ib.wsbridge.user.StB162AUserAccReadList;
import com.lloydstsb.ib.wsbridge.user.StB162BUserAccReadList;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import lb_gbo_sales.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;

public class ChannelSpecificArrangements {
    private static final Logger LOGGER = Logger.getLogger(ChannelSpecificArrangements.class);

    @Autowired
    UserClient userClient;

    @Autowired
    ExceptionUtility exceptionUtility;

    @Autowired
    BapiHeaderUserToStHeaderConverter bapiHeaderUserToStHeaderConverter;

    @Autowired
    HeaderRetriever headerRetriever;

    private static final BigInteger ACC_MOR_KEY = BigInteger.valueOf(0);

    public List<StAccountListDetail> getChannelSpecificArrangements(RequestHeader header) throws SalsaInternalServiceException, SalsaInternalResourceNotAvailableException {

        List<StAccountListDetail> accListDetail;

        StB162AUserAccReadList request = createB162Request(header);
        StB162BUserAccReadList response = retrieveB162Response(request);

        accListDetail = (null != response ? response.getAstacclistdetail() : null);

        if (null != response && null != response.getSterror() && 0 != response.getSterror().getErrorno()) {
            LOGGER.error("Internal Service Error occured in B162. Error Code | Error text" + String.valueOf(response.getSterror().getErrorno()) + " | " + response.getSterror().getErrormsg());
            throw new SalsaInternalServiceException("Response from B162 is not valid", String.valueOf(response.getSterror().getErrorno()), new ReasonText((response.getSterror().getErrormsg())));
        }
        LOGGER.info("size of AccountListDetail list returned by BAPI B162 retrieveAccessibleArrangements: " + (null != accListDetail ? accListDetail.size() : "0"));
        return accListDetail;
    }

    private StB162BUserAccReadList retrieveB162Response(StB162AUserAccReadList request) throws SalsaInternalResourceNotAvailableException {
        try {
            LOGGER.info("Calling BAPI B162 retrieveAccessibleArrangements ");
            return userClient.retrieveAccessibleArrangements(request);
        }
        catch (Exception e) {

            LOGGER.error("Exception occurred while calling B162. Returning ResourceNotAvailableError ;", e);
            throw new SalsaInternalResourceNotAvailableException(e.getMessage());
        }
    }

    private StB162AUserAccReadList createB162Request(final RequestHeader header) {
        StB162AUserAccReadList request = new StB162AUserAccReadList();
        BAPIHeader bapiHeader = headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()).getBAPIHeader();
        request.setStheader(bapiHeaderUserToStHeaderConverter.convert(bapiHeader));
        request.setAccmorekey(ACC_MOR_KEY);
        request.setBForceHostCall(false);
        return request;
    }
}
