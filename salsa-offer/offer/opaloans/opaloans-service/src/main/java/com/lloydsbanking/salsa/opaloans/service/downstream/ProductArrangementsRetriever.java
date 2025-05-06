package com.lloydsbanking.salsa.opaloans.service.downstream;

import com.lloydsbanking.salsa.downstream.loan.client.LoanClient;
import com.lloydsbanking.salsa.downstream.loan.client.b237.B237RequestBuilder;
import com.lloydsbanking.salsa.downstream.loan.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.opaloans.LoanAppStatus;
import com.lloydsbanking.salsa.soap.fs.loan.StHeader;
import com.lloydsbanking.salsa.soap.fs.loan.StLoanSavedSummary;
import com.lloydstsb.ib.wsbridge.loan.StB237ALoanSavedListGet;
import com.lloydstsb.ib.wsbridge.loan.StB237BLoanSavedListGet;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.WebServiceException;
import java.math.BigInteger;
import java.util.List;

@Component
public class ProductArrangementsRetriever {
    private static final Logger LOGGER = Logger.getLogger(ProductArrangementsRetriever.class);

    private static final String ST_PARTY_HOST = "T";

    @Autowired
    LoanClient loanClient;

    @Autowired
    BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverter;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    ExceptionUtility exceptionUtility;

    public boolean retrieveProductArrangements(final RequestHeader requestHeader, final String customerIdentifier, final String partyId, CustomerScore customerScore) throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        StB237ALoanSavedListGet b237Request = createB237Request(requestHeader, customerIdentifier, partyId);
        StB237BLoanSavedListGet b237Response = retrieveB237Response(b237Request);
        boolean isDuplicateLoanExists = false;

        if (null != b237Response) {
            if (null != b237Response.getSterror() && b237Response.getSterror().getErrorno() != 0) {
                LOGGER.error("Returning ExternalServiceError after calling BAPI B237 loanSavedList. ErrorNo | ErrorMsg ; " + b237Response.getSterror().getErrorno() + " | " + b237Response.getSterror().getErrormsg());
                throw exceptionUtility.externalServiceError(String.valueOf(b237Response.getSterror().getErrorno()), b237Response.getSterror().getErrormsg());
            } else {
                isDuplicateLoanExists = isDuplicateSavedLoanExists(b237Response.getAstloansavedsummary(), customerScore);
            }
        }
        return isDuplicateLoanExists;
    }

    private boolean isDuplicateSavedLoanExists(final List<StLoanSavedSummary> loanSavedSummaryList, CustomerScore customerScore) {
        if (!CollectionUtils.isEmpty(loanSavedSummaryList)) {
            for (StLoanSavedSummary savedSummary : loanSavedSummaryList) {
                if (null != LoanAppStatus.getLoanAppStatus(savedSummary.getLoanappstatus())) {
                    if (!StringUtils.isEmpty(loanSavedSummaryList.get(0).getCreditscoreno())) {
                        customerScore.setScoreIdentifier(loanSavedSummaryList.get(0).getCreditscoreno());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private StB237ALoanSavedListGet createB237Request(final RequestHeader requestHeader, final String customerIdentifier, final String partyId) {
        B237RequestBuilder builder = new B237RequestBuilder();
        StHeader stHeader = bapiHeaderToStHeaderConverter.convert(headerRetriever.getBapiInformationHeader(requestHeader.getLloydsHeaders()).getBAPIHeader(), headerRetriever.getServiceRequest(requestHeader), headerRetriever.getContactPoint(requestHeader).getContactPointId());
        builder.stHeader(stHeader);
        BigInteger customerId = (null != customerIdentifier ? new BigInteger(customerIdentifier) : null);
        builder.stPartyDetails(customerId, partyId, ST_PARTY_HOST);
        builder.populateCache(false);
        return builder.build();
    }

    private StB237BLoanSavedListGet retrieveB237Response(final StB237ALoanSavedListGet b237Request) throws ResourceNotAvailableErrorMsg {
        try {
            return loanClient.loanSavedList(b237Request);
        } catch (WebServiceException e) {
            LOGGER.error("Exception while calling BAPI B237 loanSavedList. Returning ResourceNotAvailableError ; ", e);
            throw exceptionUtility.resourceNotAvailableError(e.getMessage());
        }
    }
}
