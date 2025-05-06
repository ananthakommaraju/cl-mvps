package com.lloydsbanking.salsa.opaloans.service.downstream;


import com.lloydsbanking.salsa.downstream.loan.client.LoanClient;
import com.lloydsbanking.salsa.downstream.loan.client.b231.B231RequestBuilder;
import com.lloydsbanking.salsa.downstream.loan.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.opaloans.service.convert.B231ResponseToEligibleProductsConverter;
import com.lloydsbanking.salsa.soap.fs.loan.StHeader;
import com.lloydstsb.ib.wsbridge.loan.StB231ALoanPartyProductsGet;
import com.lloydstsb.ib.wsbridge.loan.StB231BLoanPartyProductsGet;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.WebServiceException;
import java.math.BigInteger;

@Component
public class EligibleProductsRetriever {
    private static final Logger LOGGER = Logger.getLogger(EligibleProductsRetriever.class);

    private static final String ST_PARTY_HOST = "T";

    @Autowired
    LoanClient loanClient;

    @Autowired
    BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverter;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    ExceptionUtility exceptionUtility;

    @Autowired
    B231ResponseToEligibleProductsConverter b231ResponseToEligibleProductsConverter;

    public void fetchEligibleLoanProducts(RequestHeader requestHeader, FinanceServiceArrangement productArrangement) throws OfferException {
        Customer customer = productArrangement.getPrimaryInvolvedParty();
        StB231ALoanPartyProductsGet b231Request = createB231Request(requestHeader, customer.getCustomerIdentifier(), customer.getCidPersID());
        StB231BLoanPartyProductsGet b231Response = null;
        try {
            b231Response = callEligibleLoanProducts(b231Request);
        }
        catch (ResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg) {
            throw new OfferException(resourceNotAvailableErrorMsg);
        }

        if (null != b231Response) {
            if (null != b231Response.getSterror() && b231Response.getSterror().getErrorno() != 0) {
                LOGGER.error("Returning ExternalBusinessError after calling BAPI B231. ErrorNo | ErrorMsg ; " + b231Response.getSterror().getErrorno() + " | " + b231Response.getSterror().getErrormsg());
                try {
                    throw exceptionUtility.externalBusinessError(String.valueOf(b231Response.getSterror().getErrorno()), b231Response.getSterror().getErrormsg());
                }
                catch (ExternalBusinessErrorMsg externalBusinessErrorMsg) {
                    throw new OfferException(externalBusinessErrorMsg);
                }
            } else {
                b231ResponseToEligibleProductsConverter.convert(b231Response, productArrangement);
            }
        }
    }

    private StB231ALoanPartyProductsGet createB231Request(RequestHeader requestHeader, String customerIdentifier, String partyIdentifier) {
        B231RequestBuilder builder = new B231RequestBuilder();
        StHeader stHeader = bapiHeaderToStHeaderConverter.convert(headerRetriever.getBapiInformationHeader(requestHeader.getLloydsHeaders()).getBAPIHeader(), headerRetriever.getServiceRequest(requestHeader), headerRetriever.getContactPoint(requestHeader).getContactPointId());
        builder.stHeader(stHeader);
        if (!StringUtils.isEmpty(customerIdentifier)) {
            builder.stParty(ST_PARTY_HOST, new BigInteger(customerIdentifier), partyIdentifier);
        } else {
            builder.stParty(ST_PARTY_HOST, BigInteger.ZERO, partyIdentifier);
        }
        builder.bNewApplication(true);
        return builder.build();
    }

    private StB231BLoanPartyProductsGet callEligibleLoanProducts(StB231ALoanPartyProductsGet request) throws ResourceNotAvailableErrorMsg {
        try {
            LOGGER.info("Calling BAPI B231 LoanPartyProductsGet");
            return loanClient.retrieveEligibleLoanProducts(request);
        } catch (WebServiceException e) {
            LOGGER.error("Exception occurred while calling BAPI B231 LoanPartyProductsGet. Returning ResourceNotAvailableError ; ", e);
            throw exceptionUtility.resourceNotAvailableError(e.getMessage());
        }
    }
}
