package com.lloydsbanking.salsa.ppae.service.downstream;

import com.lloydsbanking.salsa.downstream.loan.client.LoanClient;
import com.lloydsbanking.salsa.ppae.service.convert.B232RequestFactory;
import com.lloydsbanking.salsa.soap.pad.f263.objects.F263Resp;
import com.lloydstsb.ib.wsbridge.loan.StB232ALoanCCASign;
import com.lloydstsb.ib.wsbridge.loan.StB232BLoanCCASign;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceException;

@Repository
public class EstablishFinanceServiceArrangementRetriever {
    private static final Logger LOGGER = Logger.getLogger(EstablishFinanceServiceArrangementRetriever.class);
    @Autowired
    B232RequestFactory b232RequestFactory;
    @Autowired
    LoanClient loanClient;

    public void retrieve(RequestHeader requestHeader, F263Resp f263Resp, XMLGregorianCalendar lastModifiedDate) throws DatatypeConfigurationException {
        StB232ALoanCCASign stB232Request = b232RequestFactory.convert(requestHeader, f263Resp, lastModifiedDate);
        StB232BLoanCCASign stB232Response;
        try {
            LOGGER.info("Entering establishFinanceServiceArrangement ArrangementSetUp");
            stB232Response = loanClient.b232LoanCCASign(stB232Request);
            if (stB232Response.getSterror() != null && stB232Response.getSterror().getErrorno() != 0) {
                LOGGER.info("Error while invoking B232 External Service Error - Prepare Finance Service Arrangement Error : " + stB232Response.getSterror().getErrormsg() + " : " + stB232Response.getSterror().getErrorno());
            }
        } catch (WebServiceException e) {
            LOGGER.info("Error while invoking B232 WebService Error - Prepare Finance Service Arrangement" + e);
        }

    }
}
