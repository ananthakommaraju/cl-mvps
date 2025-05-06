package com.lloydsbanking.salsa.activate.registration.converter;

import com.lloydsbanking.salsa.brand.Brand;
import com.lloydsbanking.salsa.downstream.application.converter.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fs.application.StAccount;
import com.lloydsbanking.salsa.soap.fs.application.StHeader;
import com.lloydstsb.ib.wsbridge.application.StB750AAppPerCCRegCreate;
import com.lloydstsb.ib.wsbridge.application.StB750BAppPerCCRegCreate;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

@Component
public class B750RequestResponseConverter {

    @Autowired
    public
    HeaderRetriever headerRetriever;

    @Autowired
    public
    BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverter;


    public StB750AAppPerCCRegCreate createB750Request(RequestHeader header, Customer primaryInvolvedParty, String accountNumber, int marketingPreferenceIndicator, String prodType) {
        StB750AAppPerCCRegCreate b750Request = new StB750AAppPerCCRegCreate();

        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header);
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header);
        ContactPoint contactPoint = headerRetriever.getContactPoint(header);
        Brand brand = Brand.fromString(header.getChannelId());
        StHeader stHeader = bapiHeaderToStHeaderConverter.convertSalesUnauthHeader(bapiInformation.getBAPIHeader(), serviceRequest,
                contactPoint.getContactPointId(), mapOcisId(primaryInvolvedParty), brand);
        b750Request.setStheader(stHeader);
        b750Request.setPbktyp("N");
        b750Request.setStacc(mapStAccount(primaryInvolvedParty, accountNumber, prodType));

        if (null != primaryInvolvedParty) {
            b750Request.setEmailaddr(primaryInvolvedParty.getEmailAddress());
            b750Request.setPwdEmergingChannel(primaryInvolvedParty.getPassword());
            b750Request.setPostcode(mapPostcode(primaryInvolvedParty.getPostalAddress()));
            mapCustomerNameAndDOB(b750Request, primaryInvolvedParty);
        }
        b750Request.setMktgindEmail(marketingPreferenceIndicator);


        return b750Request;
    }

    private String mapPostcode(List<PostalAddress> postalAddress) {
        String postCode = "";
        if (postalAddress != null && !postalAddress.isEmpty()) {
            PostalAddress address = postalAddress.get(0);
            if (address.isIsPAFFormat()) {
                if (address.getStructuredAddress() != null
                        && address.getStructuredAddress().getPostCodeOut() != null
                        && address.getStructuredAddress().getPostCodeIn() != null) {

                    postCode = address.getStructuredAddress().getPostCodeOut() + address.getStructuredAddress().getPostCodeIn();
                }
            } else {
                if (address.getUnstructuredAddress() != null) {
                    postCode = address.getUnstructuredAddress().getPostCode();
                }
            }
        }
        return postCode;
    }

    private void mapCustomerNameAndDOB(StB750AAppPerCCRegCreate b750Req, Customer customer) {
        if (null != customer.getIsPlayedBy()) {
            b750Req.setDateOfBirth(customer.getIsPlayedBy().getBirthDate());

            if (!CollectionUtils.isEmpty(customer.getIsPlayedBy().getIndividualName())) {

                IndividualName name = customer.getIsPlayedBy().getIndividualName().get(0);
                b750Req.setFirstname(name.getFirstName());
                b750Req.setSurname(name.getLastName());
                b750Req.setTitle(name.getPrefixTitle());
                for (String middleName : name.getMiddleNames()) {
                    b750Req.setOthernames(middleName);
                }
            }
        }
    }

    private StAccount mapStAccount(Customer primaryInvolvedParty, String accountNumber, String prodType) {
        StAccount stAccount = new StAccount();
        stAccount.setAccno(accountNumber);
        stAccount.setProdtype(prodType);
        if (null != primaryInvolvedParty && null != primaryInvolvedParty.getExistingSortCode()) {
            stAccount.setSortcode(primaryInvolvedParty.getExistingSortCode());
        }
        return stAccount;

    }

    private BigInteger mapOcisId(Customer primaryInvolvedParty) {
        BigInteger ocisId = null;
        if (!StringUtils.isEmpty(primaryInvolvedParty.getCustomerIdentifier())) {
            ocisId = new BigInteger(primaryInvolvedParty.getCustomerIdentifier());
        }
        return ocisId;
    }



    public void mapB750ResponseAttributesToProductArrangement(Customer primaryInvolvedParty, StB750BAppPerCCRegCreate b750Response) {
        InternetBankingRegistration registeredIn = new InternetBankingRegistration();
        if (null != b750Response.getAppid()) {
            registeredIn.setRegistrationIdentifier(b750Response.getAppid().toString());
        }
        if (null != b750Response.getAppverNew()) {
            registeredIn.setApplicationVersion(b750Response.getAppverNew().toString());
        }
        primaryInvolvedParty.setIsRegisteredIn(registeredIn);

    }
}
