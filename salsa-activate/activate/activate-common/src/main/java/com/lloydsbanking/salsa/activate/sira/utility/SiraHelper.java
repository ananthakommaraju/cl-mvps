package com.lloydsbanking.salsa.activate.sira.utility;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.pam.exception.ExceptionUtility;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ApplicationActivityHistoryDao;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferenceDataLookUpDao;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationActivityHistory;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.pam.model.TmxDetails;
import lib_sim_bo.businessobjects.CustomerDeviceDetails;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.*;

@Component
public class SiraHelper {
    private static final Logger LOGGER = Logger.getLogger(SiraHelper.class);
    private static final int LOWER_THRESHOLD_LIMIT_FOR_ACCEPT = 0;
    private static final int UPPER_THRESHOLD_LIMIT_FOR_ACCEPT = 1;
    private static final int LOWER_THRESHOLD_LIMIT_FOR_REFER_FRAUD = 2;
    private static final int UPPER_THRESHOLD_LIMIT_FOR_REFER_FRAUD = 3;
    private static final int LOWER_THRESHOLD_LIMIT_FOR_REFER_IDV = 4;
    private static final int UPPER_THRESHOLD_LIMIT_FOR_REFER_IDV = 5;
    private static final int LOWER_THRESHOLD_LIMIT_FOR_DECLINE = 6;
    private static final List<String> APP_COMPLETION_APPLICATION_STATUS = Arrays.asList("1001");
    private static final List<String> FULFILMENT_APPLICATION_STATUS = Arrays.asList("1010");
    @Autowired
    ReferenceDataLookUpDao referenceDataLookUpDao;
    @Autowired
    ExceptionUtility exceptionUtility;
    @Autowired
    ApplicationActivityHistoryDao applicationActivityHistoryDao;
    @Autowired
    DateFactory dateFactory;

    @Transactional(readOnly = true)
    public List<ReferenceDataLookUp> getLookupListFromChannelAndGroupCodeListAndSequence(String channel, List<String> groupCdList) throws DataNotAvailableErrorMsg {
        List<ReferenceDataLookUp> lookupList = referenceDataLookUpDao.findByChannelAndGroupCodeInOrderBySequenceAsc(channel, groupCdList);
        if (org.springframework.util.CollectionUtils.isEmpty(lookupList)) {
            throw exceptionUtility.dataNotAvailableError(groupCdList.get(0), "GROUP_CODE", "REF_DATA_LOOKUP_VW", "No matching records found, error code: ");
        }
        return lookupList;
    }

    private CustomerDeviceDetails tmxDetailsToCustomerConverter(String deviceData) {
        CustomerDeviceDetails customerDeviceDetails;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(CustomerDeviceDetails.class);
            ByteArrayInputStream input = new ByteArrayInputStream(deviceData.getBytes(Charset.forName("UTF-8")));
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<CustomerDeviceDetails> root = unmarshaller.unmarshal(new StreamSource(input), CustomerDeviceDetails.class);
            customerDeviceDetails = root.getValue();
        } catch (JAXBException exc) {
            LOGGER.info("Exception while unmarshalling tmx details data", exc);
            return null;
        }
        return customerDeviceDetails;
    }

    public String getSiraResultStatus(List<ReferenceDataLookUp> lookUpList, BigInteger totalScore) {
        if (checkLimit(lookUpList.get(LOWER_THRESHOLD_LIMIT_FOR_ACCEPT).getLookupValueDesc(), lookUpList.get(UPPER_THRESHOLD_LIMIT_FOR_ACCEPT).getLookupValueDesc(), totalScore)) {
            return SiraStatus.ACCEPT.getValue();
        } else if (checkLimit(lookUpList.get(LOWER_THRESHOLD_LIMIT_FOR_REFER_FRAUD).getLookupValueDesc(), lookUpList.get(UPPER_THRESHOLD_LIMIT_FOR_REFER_FRAUD).getLookupValueDesc(), totalScore)) {
            return SiraStatus.REFER_FRAUD.getValue();
        } else if (checkLimit(lookUpList.get(LOWER_THRESHOLD_LIMIT_FOR_REFER_IDV).getLookupValueDesc(), lookUpList.get(UPPER_THRESHOLD_LIMIT_FOR_REFER_IDV).getLookupValueDesc(), totalScore)) {
            return SiraStatus.REFER_IDV.getValue();
        } else if (new BigInteger(lookUpList.get(LOWER_THRESHOLD_LIMIT_FOR_DECLINE).getLookupValueDesc()).compareTo(totalScore) <= 0) {
            return SiraStatus.DECLINE.getValue();
        }
        return null;
    }

    private boolean checkLimit(String lowerLimit, String upperLimit, BigInteger totalScore) {
        return new BigInteger(lowerLimit).compareTo(totalScore) <= 0 && new BigInteger(upperLimit).compareTo(totalScore) >= 0;
    }

    public void addCustomerDeviceDetails(Individual individual, TmxDetails tmxDetails) {
        CustomerDeviceDetails customerDeviceDetails = tmxDetailsToCustomerConverter(tmxDetails.getDeviceData());
        if (customerDeviceDetails != null) {
            if (individual.getCustomerDeviceDetails() == null) {
                individual.setCustomerDeviceDetails(new CustomerDeviceDetails());
            }
            individual.getCustomerDeviceDetails().setAccountLogin(customerDeviceDetails.getAccountLogin());
            individual.getCustomerDeviceDetails().setBrowserLanguage(customerDeviceDetails.getBrowserLanguage());
            individual.getCustomerDeviceDetails().setCustomerDecision(customerDeviceDetails.getCustomerDecision());
            individual.getCustomerDeviceDetails().setDeviceFirstSeen(customerDeviceDetails.getDeviceFirstSeen());
            individual.getCustomerDeviceDetails().setDeviceLastEvent(customerDeviceDetails.getDeviceLastEvent());
            individual.getCustomerDeviceDetails().setDnsIPGeo(customerDeviceDetails.getDnsIPGeo());
            individual.getCustomerDeviceDetails().setExactDeviceId(customerDeviceDetails.getExactDeviceId());
            individual.getCustomerDeviceDetails().setProxyIpGeo(customerDeviceDetails.getProxyIpGeo());
            individual.getCustomerDeviceDetails().setSmartDeviceId(customerDeviceDetails.getSmartDeviceId());
            individual.getCustomerDeviceDetails().setSmartDeviceIdConfidence(customerDeviceDetails.getSmartDeviceIdConfidence());
            individual.getCustomerDeviceDetails().setTmxPolicyScore(customerDeviceDetails.getTmxPolicyScore());
            individual.getCustomerDeviceDetails().setTrueIpOrganization(customerDeviceDetails.getTrueIpOrganization());
            individual.getCustomerDeviceDetails().setTrueIpIsp(customerDeviceDetails.getTrueIpIsp());
            individual.getCustomerDeviceDetails().setTmxReviewStatus(customerDeviceDetails.getTmxReviewStatus());
            individual.getCustomerDeviceDetails().setTmxReasonCode(customerDeviceDetails.getTmxReasonCode());
            individual.getCustomerDeviceDetails().setTmxSummaryRiskScore(customerDeviceDetails.getTmxSummaryRiskScore());
            individual.getCustomerDeviceDetails().setTrueIp(customerDeviceDetails.getTrueIp());
            individual.getCustomerDeviceDetails().setTmxRiskRating(customerDeviceDetails.getTmxRiskRating());
            individual.getCustomerDeviceDetails().setTmxSummaryReasonCode(customerDeviceDetails.getTmxSummaryReasonCode());
            individual.getCustomerDeviceDetails().setTrueIpGeo(customerDeviceDetails.getTrueIpGeo());
        }
    }

    private XMLGregorianCalendar dateToXMLGregorianCalendarBST(Date date) {
        try {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeZone(TimeZone.getTimeZone("Europe/London"));
            gc.setTime(date);
            XMLGregorianCalendar gregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            gregorianCalendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            return gregorianCalendar;
        } catch (DatatypeConfigurationException e) {
            LOGGER.info("Exception caught ", e);
        }
        return null;
    }

    public XMLGregorianCalendar setFulfilmentDate(String applicationStatus, String arrangementId) {
        if (("1010").equalsIgnoreCase(applicationStatus)) {
            return setDate(arrangementId, FULFILMENT_APPLICATION_STATUS);
        } else {
            return dateToXMLGregorianCalendarBST(new Date());
        }
    }

    private XMLGregorianCalendar setDate(String arrangementId, List<String> appStatus) {
        List<ApplicationActivityHistory> applicationActivityHistoryList = applicationActivityHistoryDao.findByApplicationsIdAndApplicationStatusStatusInOrderByDateModifiedAsc(Long.valueOf(arrangementId), appStatus);
        if (!CollectionUtils.isEmpty(applicationActivityHistoryList) && applicationActivityHistoryList.get(0) != null) {
            try {
                return dateFactory.dateToXMLGregorianCalendar(applicationActivityHistoryList.get(0).getDateModified());
            } catch (DatatypeConfigurationException e) {
                LOGGER.info("Exception caught ", e);
            }
        }
        return null;
    }

    public XMLGregorianCalendar setAppDate(String arrangementId) {
        return setDate(arrangementId, APP_COMPLETION_APPLICATION_STATUS);
    }
}
