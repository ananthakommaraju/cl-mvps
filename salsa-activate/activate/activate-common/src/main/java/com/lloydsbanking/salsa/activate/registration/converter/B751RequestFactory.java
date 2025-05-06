package com.lloydsbanking.salsa.activate.registration.converter;

import com.lloydsbanking.salsa.brand.Brand;
import com.lloydsbanking.salsa.brand.Channel;
import com.lloydsbanking.salsa.downstream.application.converter.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fs.application.StAccount;
import com.lloydsbanking.salsa.soap.fs.application.StHeader;
import com.lloydstsb.ib.wsbridge.application.StB751AAppPerCCRegAuth;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;


@Component
public class B751RequestFactory {

    private static final String ARRANGEMENT_TYPE_CREDIT_CARD = "CC";

    private static final String PRODUCT_TYPE_CARD = "C";

    private static final String PRODUCT_TYPE_ACCOUNT = "A";

    private static final String CUSTOMER_SEGMENT_NON_FRANCHISED = "3";

    @Autowired
    BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverter;

    @Autowired
    HeaderRetriever headerRetriever;

    public StB751AAppPerCCRegAuth convert(ProductArrangement productArrangement, RequestHeader requestHeader) {

        StB751AAppPerCCRegAuth request = new StB751AAppPerCCRegAuth();
        BigInteger ocisId = null;

        if (productArrangement.getPrimaryInvolvedParty() != null) {
            if (!StringUtils.isEmpty(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier())) {
                ocisId = new BigInteger(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier());
            }
            if (productArrangement.getPrimaryInvolvedParty().getCustomerSegment() != null &&
                    CUSTOMER_SEGMENT_NON_FRANCHISED.equals(productArrangement.getPrimaryInvolvedParty().getCustomerSegment())) {
                request.setBNewToBank(true);
            } else {
                request.setBNewToBank(false);
            }
            if (productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn() != null) {
                if (productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().getRegistrationIdentifier() != null) {
                    request.setAppid(new BigInteger(productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().
                            getRegistrationIdentifier()));
                }
                if (!StringUtils.isEmpty(productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().getApplicationVersion())) {
                    request.setAppver(new BigInteger(productArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().
                            getApplicationVersion()));
                }
            }
        }

        StHeader stHeader = getStHeader(requestHeader, ocisId);
        StAccount stAccount = new StAccount();

        if (productArrangement.getArrangementType() != null && ARRANGEMENT_TYPE_CREDIT_CARD.equalsIgnoreCase(productArrangement.getArrangementType())) {
            stAccount.setProdtype(PRODUCT_TYPE_CARD);
        } else {
            stAccount.setProdtype(PRODUCT_TYPE_ACCOUNT);
        }

        stAccount.setHost(stHeader.getStpartyObo().getHost());
        stAccount.setAccno(productArrangement.getAccountNumber());
        request.setStaccount(stAccount);
        request.setStparty(stHeader.getStpartyObo());
        request.setStheader(stHeader);
        return request;
    }

    private StHeader getStHeader(RequestHeader requestHeader, BigInteger ocisId) {
        BAPIHeader bapiHeader = headerRetriever.getBapiInformationHeader(requestHeader).getBAPIHeader();
        String contactPointId = headerRetriever.getContactPoint(requestHeader).getContactPointId();
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader);
        Brand brand = Channel.getBrandForChannel(Channel.fromString(bapiHeader.getChanid()));
        StHeader stHeader = bapiHeaderToStHeaderConverter.convertSalesUnauthHeader(bapiHeader, serviceRequest, contactPointId, ocisId, brand);
        String userIdAuthor = String.format("%-25s", bapiHeader.getUseridAuthor());
        stHeader.setUseridAuthor(userIdAuthor);
        return stHeader;
    }
}
