package com.lloydsbanking.salsa.offer.identify.convert;

import com.lloydsbanking.salsa.constant.Gender;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Resp;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.IndividualName;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class F061ToInvolvedPartyDetailsResponseConverter {
    private static final Logger LOGGER = Logger.getLogger(F061ToInvolvedPartyDetailsResponseConverter.class);

    @Autowired
    PostalAddressConverter postalAddressConverter;

    @Autowired
    public AuditDataFactory auditDataFactory;

    @Autowired
    ExceptionUtility exceptionUtility;

    private static final String DEFAULT_RESIDENTIAL_STATUS_CODE = "000";

    private static final String DEFAULT_MARITAL_STATUS_CODE = "000";

    private static final String DEFAULT_EMPLOYMENT_STATUS_CODE = "008";

    public void setInvolvedPartyDetailsResponse(Customer customer, boolean isAuthCustomer, F061Resp retrieveInvolvedPartyDetailsResponse) throws InternalServiceErrorMsg {
        LOGGER.info("Inside setInvolvedPartyDetailsResponse");
        setPostalAddress(customer, retrieveInvolvedPartyDetailsResponse, isAuthCustomer);
        if (null != retrieveInvolvedPartyDetailsResponse.getPartyEnqData()) {
            setPersonalDetails(customer, retrieveInvolvedPartyDetailsResponse);
            if (null != retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPersonalData()) {
                customer.setCidPersID(retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPersonalData().getCIDPersId());
            }
        }
        customer.setSourceSystemId("3");
        setAuditDetails(customer, retrieveInvolvedPartyDetailsResponse);
        setStaffIndicator(customer, retrieveInvolvedPartyDetailsResponse);
    }

    private void setPostalAddress(Customer customer, F061Resp retrieveInvolvedPartyDetailsResponse, boolean isAuthCustomer) throws InternalServiceErrorMsg {
        try {
            List<PostalAddress> postalAddresslist = new ArrayList<>();
            PostalAddress postalAddressCurrent = new PostalAddress();
            PostalAddress postalAddressPrevious = null;
            for (PostalAddress postalAddress : customer.getPostalAddress()) {
                if ("CURRENT".equals(postalAddress.getStatusCode())) {
                    if (isAuthCustomer) {
                        PostalAddress postalAddressFromOcis = postalAddressConverter.getPostalAddress(retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getAddressData());
                        postalAddressFromOcis.setDurationofStay(postalAddress.getDurationofStay());
                        postalAddressCurrent = postalAddressFromOcis;
                    } else {
                        postalAddressCurrent = postalAddress;
                    }
                } else {
                    postalAddressPrevious = postalAddress;

                }
            }
            postalAddresslist.add(postalAddressCurrent);
            if (postalAddressPrevious != null) {
                postalAddresslist.add(postalAddressPrevious);
            }
            customer.getPostalAddress().clear();
            customer.getPostalAddress().addAll(postalAddresslist);
        } catch (ParseException | DatatypeConfigurationException e) {
            LOGGER.info("Exception caught while parsing date: ", e);
            throw exceptionUtility.internalServiceError(null, e.getMessage());
        }
    }

    private void setPersonalDetails(Customer customer, F061Resp retrieveInvolvedPartyDetailsResponse) {
        if (null != retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPartyNonCoreData()) {
            customer.getIsPlayedBy().setMaritalStatus(getMaritalStatus(customer, retrieveInvolvedPartyDetailsResponse));
            customer.getIsPlayedBy().setResidentialStatus(getResidentialStatus(customer, retrieveInvolvedPartyDetailsResponse));
            customer.getIsPlayedBy().setEmploymentStatus(getEmploymentStatus(customer, retrieveInvolvedPartyDetailsResponse));
        }
        if (null != retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPersonalData() && null == customer.getIsPlayedBy().getGender()) {
            customer.getIsPlayedBy().setGender(getGender(retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPersonalData().getGenderCd()));
        }
    }

    private String getGender(String genderCd) {
        if (Gender.MALE.getValue().equals(genderCd)) {
            return Gender.MALE.getKey();
        } else if (Gender.FEMALE.getValue().equals(genderCd)) {
            return Gender.FEMALE.getKey();
        } else {
            return genderCd;
        }
    }

    private String getEmploymentStatus(Customer customer, F061Resp retrieveInvolvedPartyDetailsResponse) {
        if (null == customer.getIsPlayedBy().getEmploymentStatus()) {
            if (null != retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPartyNonCoreData().getEmploymentStatusCd()) {
                return String.format("%03d", retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPartyNonCoreData().getEmploymentStatusCd());
            }
            return DEFAULT_EMPLOYMENT_STATUS_CODE;
        } else {
            return customer.getIsPlayedBy().getEmploymentStatus();
        }
    }

    private String getResidentialStatus(Customer customer, F061Resp retrieveInvolvedPartyDetailsResponse) {
        if (null == customer.getIsPlayedBy().getResidentialStatus()) {
            if (null != retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPartyNonCoreData().getResidStatusCd()) {
                return String.format("%03d", retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPartyNonCoreData().getResidStatusCd());
            }
            return DEFAULT_RESIDENTIAL_STATUS_CODE;
        } else {
            return customer.getIsPlayedBy().getResidentialStatus();
        }
    }

    private String getMaritalStatus(Customer customer, F061Resp retrieveInvolvedPartyDetailsResponse) {
        if (null == customer.getIsPlayedBy().getMaritalStatus()) {
            if (null != retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPartyNonCoreData().getMaritalStatusCd()) {
                return String.format("%03d", retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPartyNonCoreData().getMaritalStatusCd());
            }
            return DEFAULT_MARITAL_STATUS_CODE;
        } else {
            return customer.getIsPlayedBy().getMaritalStatus();
        }
    }

    private void setAuditDetails(Customer customer, F061Resp retrieveInvolvedPartyDetailsResponse) {
        if (null != retrieveInvolvedPartyDetailsResponse.getPartyEnqData()) {
            customer.getAuditData().addAll(auditDataFactory.getAuditData(retrieveInvolvedPartyDetailsResponse.getPartyEnqData()));
        }
    }

    private void setStaffIndicator(Customer customer, F061Resp retrieveInvolvedPartyDetailsResponse) {
        customer.getIsPlayedBy().setIsStaffMember((null != retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPartyNonCoreData() && retrieveInvolvedPartyDetailsResponse
                .getPartyEnqData()
                .getPartyNonCoreData()
                .getStaffIn()
                .equals("1")));
    }

    public void setInvolvedPartyDetailsResponseForLoans(Customer customer, F061Resp retrieveInvolvedPartyDetailsResponse) throws InternalServiceErrorMsg {
        LOGGER.info("inside setInvolvedPartyDetailsresponse for Loans");
        try {
            customer.getPostalAddress().clear();
            customer.getPostalAddress().add(postalAddressConverter.getPostalAddress(retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getAddressData()));
        } catch (ParseException | DatatypeConfigurationException e) {
            LOGGER.info("Offer: Identify: Exception caught while parsing date : ", e);
            throw exceptionUtility.internalServiceError(null, e.getMessage());
        }
        if (null != retrieveInvolvedPartyDetailsResponse.getPartyEnqData()) {
            setPersonalDetails(customer, retrieveInvolvedPartyDetailsResponse);
            if (null != retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPersonalData()) {
                if (null == customer.getIsPlayedBy()) {
                    customer.setIsPlayedBy(new Individual());
                }
                if (CollectionUtils.isEmpty(customer.getIsPlayedBy().getIndividualName())) {
                    customer.getIsPlayedBy().getIndividualName().add(0, new IndividualName());
                }
                IndividualName name = customer.getIsPlayedBy().getIndividualName().get(0);
                customer.setCidPersID(retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPersonalData().getCIDPersId());
                name.setFirstName(retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPersonalData().getFirstForeNm());
                name.setLastName(retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPersonalData().getSurname());
                name.setPrefixTitle(retrieveInvolvedPartyDetailsResponse.getPartyEnqData().getPersonalData().getPartyTl());
            }
        }
        customer.setSourceSystemId("3");
        setAuditDetails(customer, retrieveInvolvedPartyDetailsResponse);
        setStaffIndicator(customer, retrieveInvolvedPartyDetailsResponse);
    }
}
