package com.lloydsbanking.salsa.ppae.service.convert;


import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.loan.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fs.loan.StHeader;
import com.lloydsbanking.salsa.soap.fs.loan.StLoanDetails1;
import com.lloydsbanking.salsa.soap.fs.loan.StLoanHeader;
import com.lloydsbanking.salsa.soap.fs.loan.StLoanIllustRequest;
import com.lloydsbanking.salsa.soap.pad.q028.objects.Q028Resp;
import com.lloydstsb.ib.wsbridge.loan.StB233ALoanIllustrate;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;

@Component
public class B233RequestFactory {
    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    BapiHeaderToStHeaderConverter headerConverter;

    private static final Logger LOGGER = Logger.getLogger(B233RequestFactory.class);

    public StB233ALoanIllustrate convert(Q028Resp q028Resp, RequestHeader requestHeader) {
        StB233ALoanIllustrate b233Request = new StB233ALoanIllustrate();
        StHeader stHeader = headerConverter.convert(headerRetriever.getBapiInformationHeader(requestHeader.getLloydsHeaders()).getBAPIHeader(), headerRetriever.getServiceRequest(requestHeader), headerRetriever.getContactPoint(requestHeader).getContactPointId());
        b233Request.setStheader(stHeader);
        b233Request.setStloanheader(getStLoanHeader(q028Resp));
        b233Request.setStloanillreq(getStLoanIllustRequest(q028Resp));
        b233Request.setBNewApplication(true);
        b233Request.setBAvoidWriteToPAD(false);
        DateFactory dateFactory = new DateFactory();
        FastDateFormat fastDateFormat = FastDateFormat.getInstance("ddMMyyyyHHmmss");
        b233Request.setTmstmpLastPadUpdate(dateFactory.stringToXMLGregorianCalendar(q028Resp.getOutIdentifiers().getUpdateTs(), fastDateFormat));
        b233Request.setLoantermProdMax(new BigInteger("84"));
        String applicationStatus = ActivateCommonConstant.AsmDecision.ACCEPT.equalsIgnoreCase(q028Resp.getApplicationDetails().getASMCreditScoreResultCd()) ? "9" : "2";
        b233Request.setLoanpadstatus(applicationStatus);
        b233Request.setLoanpurpose(q028Resp.getApplicationDetails().getLoanPurposeCd());
        return b233Request;
    }

    private StLoanIllustRequest getStLoanIllustRequest(Q028Resp q028Resp) {
        StLoanIllustRequest stLoanIllustRequest = new StLoanIllustRequest();
        stLoanIllustRequest.setCreditscoreno(q028Resp.getOutIdentifiers().getRequestNo());
        stLoanIllustRequest.setAmtLoan(convertStringToBigDecimal(q028Resp.getIllustrationDetails().getLoanAm().getLoanCshAm(), "100"));
        stLoanIllustRequest.setAmtRepayment(BigDecimal.ZERO);
        stLoanIllustRequest.setLoanterm(BigInteger.valueOf(q028Resp.getIllustrationDetails().getLoanTerm().getPrimaryLoanTermDr()));
        stLoanIllustRequest.setLoantermMax(new BigInteger("84"));
        stLoanIllustRequest.setLoantermDefer(BigInteger.valueOf(q028Resp.getIllustrationDetails().getLoanTerm().getDeferredMonthsNo()));
        stLoanIllustRequest.setStloandets1(getStLoanDetails1(q028Resp));
        stLoanIllustRequest.setEmploymtstatuscd(q028Resp.getApplicantDetails().getParty().get(0).getEmploymentStatusCd());
        stLoanIllustRequest.setBNeedMonthlyExtraForLPI(false);
        stLoanIllustRequest.setBAutomaticallyGenerated(false);
        return stLoanIllustRequest;
    }

    private StLoanDetails1 getStLoanDetails1(Q028Resp q028Resp) {
        StLoanDetails1 stLoanDetails1 = new StLoanDetails1();
        stLoanDetails1.setLoanprodid(q028Resp.getIllustrationDetails().getProduct().getProductId());
        if (q028Resp.getIllustrationDetails().getProduct().getInsuranceTakeUpIn().equals("1")) {
            stLoanDetails1.setInsuranceind(1);
        }
        if (!q028Resp.getApplicantDetails().getParty().isEmpty()) {
            stLoanDetails1.setDependentscount(String.valueOf(q028Resp.getApplicantDetails().getParty().get(0).getPartyDpndntChildrnQy()));
        }
        stLoanDetails1.setCurrencycode(q028Resp.getIllustrationDetails().getProduct().getCurrencyCd());
        return stLoanDetails1;
    }

    private StLoanHeader getStLoanHeader(Q028Resp q028Resp) {
        StLoanHeader stLoanHeader = new StLoanHeader();
        stLoanHeader.setOcisid(BigInteger.valueOf(q028Resp.getApplicantDetails().getParty().get(0).getPartyId()));
        stLoanHeader.setPartyidPersId(q028Resp.getApplicantDetails().getParty().get(0).getPersId());
        stLoanHeader.setCustnum(q028Resp.getApplicantDetails().getParty().get(0).getCSExtPartyIdTx());
        return stLoanHeader;
    }

    private BigDecimal convertStringToBigDecimal(String s, String divisor) {
        BigDecimal bigDecimal = null;
        if (s != null) {
            bigDecimal = new BigDecimal(s).divide(new BigDecimal(divisor));
        }
        return bigDecimal;
    }
}
