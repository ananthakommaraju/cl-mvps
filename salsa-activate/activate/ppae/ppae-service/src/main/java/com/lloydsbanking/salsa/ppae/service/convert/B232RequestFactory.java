package com.lloydsbanking.salsa.ppae.service.convert;

import com.lloydsbanking.salsa.downstream.loan.client.b232.B232RequestBuilder;
import com.lloydsbanking.salsa.downstream.loan.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fs.loan.StCCASignLetter;
import com.lloydsbanking.salsa.soap.fs.loan.StHeader;
import com.lloydsbanking.salsa.soap.pad.f263.objects.F263Resp;
import com.lloydstsb.ib.wsbridge.loan.StB232ALoanCCASign;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;

@Component
public class B232RequestFactory {
    private static final Logger LOGGER = Logger.getLogger(B232RequestFactory.class);
    private static final BigInteger DEFAULT_QUOTE_TYPE = BigInteger.ZERO;

    private static final String DEFAULT_MODE = "2";
    private static final String DEFAULT_CCTM_SESSION_ID = "0";
    private static final String ID_UNAUTH = "UNAUTHSALE";

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    BapiHeaderToStHeaderConverter headerConverter;

    @Autowired
    StLoanCoreFactory stLoanCoreFactory;

    public StB232ALoanCCASign convert(RequestHeader requestHeader, F263Resp f263Resp, XMLGregorianCalendar lastModifiedDate) throws DatatypeConfigurationException {
        StHeader header = headerConverter.convert(headerRetriever.getBapiInformationHeader(requestHeader.getLloydsHeaders()).getBAPIHeader(), headerRetriever.getServiceRequest(requestHeader), headerRetriever.getContactPoint(requestHeader).getContactPointId());
        header.setUseridAuthor(ID_UNAUTH);
        header.getStpartyObo().setPartyid(ID_UNAUTH);
        B232RequestBuilder b232RequestBuilder = new B232RequestBuilder();
        Boolean isbBatchRetry = headerRetriever.getBapiInformationHeader(requestHeader).getBAPIOperationalVariables().isBBatchRetry();
        if (isbBatchRetry != null) {
            b232RequestBuilder.bBatchRetry(isbBatchRetry);
        }
        b232RequestBuilder.stLoanCore(stLoanCoreFactory.getStLoanDetails(f263Resp));
        b232RequestBuilder.creditScoreId(f263Resp.getApplicationDetails().getCreditScoreId());
        b232RequestBuilder.loanProdTxt(f263Resp.getIllustrationDetails().getProduct().getProductNm());
        b232RequestBuilder.cctmSessionId(DEFAULT_CCTM_SESSION_ID);
        b232RequestBuilder.stHeader(header);
        b232RequestBuilder.quoteType(DEFAULT_QUOTE_TYPE);
        b232RequestBuilder.astCcaSignLetter(getStCCASignLetter(lastModifiedDate));
        return b232RequestBuilder.build();
    }

    private StCCASignLetter getStCCASignLetter(XMLGregorianCalendar lastModifiedDate) throws DatatypeConfigurationException {
        StCCASignLetter stCCASignLetter = new StCCASignLetter();
        stCCASignLetter.setLoanmode(DEFAULT_MODE);
        if (lastModifiedDate != null) {
            XMLGregorianCalendar datPAMLstUpdt = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            datPAMLstUpdt.setYear(lastModifiedDate.getYear());
            datPAMLstUpdt.setMonth(lastModifiedDate.getMonth());
            datPAMLstUpdt.setDay(lastModifiedDate.getDay());
            stCCASignLetter.setDatPAMLstUpdt(datPAMLstUpdt);
        }
        return stCCASignLetter;
    }


}
