package com.lloydsbanking.salsa.ppae.service.downstream;

import com.lloydsbanking.salsa.downstream.loan.client.LoanClient;
import com.lloydsbanking.salsa.ppae.service.convert.B233RequestFactory;
import com.lloydsbanking.salsa.soap.pad.q028.objects.Q028Resp;
import com.lloydstsb.ib.wsbridge.loan.StB233ALoanIllustrate;
import com.lloydstsb.ib.wsbridge.loan.StB233BLoanIllustrate;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;

@Repository
public class PrepareFinanceServiceArrangement {
    private static final Logger LOGGER = Logger.getLogger(PrepareFinanceServiceArrangement.class);
    @Autowired
    B233RequestFactory b233RequestFactory;
    @Autowired
    LoanClient loanClient;

    public void process(Q028Resp q028Resp, RequestHeader requestHeader) {
        StB233ALoanIllustrate b233ALoanIllustrateRequest = b233RequestFactory.convert(q028Resp, requestHeader);
        StB233BLoanIllustrate stB233BLoanIllustrateResponse;
        try {
            LOGGER.info("Entering prepareFinanceServiceArrangementProposal");
            stB233BLoanIllustrateResponse = loanClient.b233BLoanIllustrate(b233ALoanIllustrateRequest);
            if (stB233BLoanIllustrateResponse.getSterror() != null && stB233BLoanIllustrateResponse.getSterror().getErrorno() != 0) {
                LOGGER.info("Error while invoking B233, External Service Error - Prepare Finance Service Arrangement Error : " + stB233BLoanIllustrateResponse.getSterror().getErrorno() + " : " + stB233BLoanIllustrateResponse.getSterror().getErrorno());
            }
        } catch (WebServiceException e) {
            LOGGER.info("Error while invoking B233, WebService Error - Prepare Finance Service Arrangement" + e);
        }
        LOGGER.info("Exiting prepareFinanceServiceArrangementProposal");
    }
}
