package com.lloydsbanking.salsa.activate.sira.convert;


import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ApplicationParameterValuesDao;
import com.lloydsbanking.salsa.downstream.pam.model.ApplicationParameterValues;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.sira.client.DeviceDataBuilder;
import com.lloydsbanking.salsa.downstream.sira.client.EnquiryBasicsBuilder;
import com.lloydsbanking.salsa.downstream.sira.client.PartyBuilder;
import com.synectics_solutions.sira.schemas.realtime.base.v2_0.device.DeviceDetailsType;
import com.synectics_solutions.sira.schemas.realtime.base.v2_0.enquiry.*;
import com.synectics_solutions.sira.schemas.realtime.base.v2_0.enquirybasic.AdditionalDataType;
import com.synectics_solutions.sira.schemas.realtime.base.v2_0.enquirybasic.DecisionDataType;
import com.synectics_solutions.sira.schemas.realtime.base.v2_0.enquirybasic.DecisionDetailType;
import com.synectics_solutions.sira.schemas.realtime.finance.v2_0.financeenquiry.FinanceEnquiryType;
import com.synectics_solutions.sira.schemas.realtime.finance.v2_0.financeenquirybasic.FinanceEnquiryBasicType;
import com.synectics_solutions.sira.schemas.realtime.variant.v2_0.datalibrary.ChannelType;
import com.synectics_solutions.sira.schemas.realtime.variant.v2_0.datalibrary.DecisionSourceType;
import com.synectics_solutions.sira.schemas.realtime.variant.v2_0.datalibrary.DecisionType;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.Source;
import lib_sim_bo.businessobjects.CustomerDeviceDetails;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.DepositArrangement;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.util.*;

@Component
public class SiraRequestFactory {
    private static final Logger LOGGER = Logger.getLogger(SiraRequestFactory.class);
    private static final QName ENQUIRY_QNAME = new QName("http://www.synectics-solutions.com/sira/schemas/realtime/finance/v2.0/FinanceEnquiry.xsd", "Enquiry");
    private static final QName ENQUIRY_BASIC_QNAME = new QName("http://www.synectics-solutions.com/sira/schemas/realtime/finance/v2.0/FinanceEnquiryBasic.xsd", "EnquiryBasic");
    private static final String PRODUCT_CODE = "CURRENT ACCOUNT";
    private static final String PRIMARY_PRODUCT_INDICATOR = "Y";
    private static final String ISO_COUNTRY_CODE = "ISO_COUNTRY_CODE";
    private static final String SIRA_TOTAL_RULE_SCORE = "100062";
    private static final String SIRA_DECISION = "100063";

    @Autowired
    DateFactory dateFactory;
    @Autowired
    LookUpValueRetriever lookUpValueRetriever;
    @Autowired
    ApplicationParameterValuesDao applicationParameterValuesDao;

    public Source convert(DepositArrangement depositArrangement, String channelId, String userIdAuthor, XMLGregorianCalendar appDate, XMLGregorianCalendar fulfilmentDate) {
        Source source = new Source();
        source.setSourceMessageId(String.valueOf(UUID.randomUUID()));
        source.setSourceDataId(depositArrangement.getArrangementId());

        source.setSourceData(convertSourceData(depositArrangement, channelId, userIdAuthor, appDate, fulfilmentDate));
        List<String> applicationParameters = new ArrayList<>();
        applicationParameters.add(SIRA_TOTAL_RULE_SCORE);
        applicationParameters.add(SIRA_DECISION);
        List<ApplicationParameterValues> applicationParameterValues = applicationParameterValuesDao.findByApplicationsIdAndApplicationParametersCodeIn(Long.valueOf(depositArrangement.getArrangementId()), applicationParameters);
        if (!CollectionUtils.isEmpty(applicationParameterValues) && applicationParameterValues.size() == 2 && applicationParameterValues.get(0) != null && applicationParameterValues.get(1) != null) {
            source.setSourceDataVersion(1);
        } else {
            source.setSourceDataVersion(0);
        }
        source.setDataLengthBytes(0);
        source.setSourceMessagePriority(0);
        source.setSourceMessageIssuedDateTime(getXmlGregorianDate(new Date()));
        return source;
    }

    private Source.SourceData convertSourceData(DepositArrangement depositArrangement, String channelId, String userIdAuthor, XMLGregorianCalendar appDate, XMLGregorianCalendar fulfilmentDate) {

        Source.SourceData sourceData = new Source.SourceData();
        FinanceEnquiryBasicType financeEnquiryBasic = new FinanceEnquiryBasicType();
        financeEnquiryBasic.setProductCode(PRODUCT_CODE);
        financeEnquiryBasic.setPrimaryProductIndicator(PRIMARY_PRODUCT_INDICATOR);
        if (depositArrangement.isIsJointParty() != null && depositArrangement.isIsJointParty()) {
            financeEnquiryBasic.setMultipleApplicants("Y");
        } else {
            financeEnquiryBasic.setMultipleApplicants("N");
        }
        financeEnquiryBasic.setOfferAcceptanceDate(fulfilmentDate);
        financeEnquiryBasic.setDecisions(getDecisions(depositArrangement));
        EnquiryBasicsBuilder enquiryBasicsBuilder = new EnquiryBasicsBuilder();
        AdditionalDataType enquiryBasicExtras = new AdditionalDataType();
        enquiryBasicExtras.getEnquiryBasicExtra().addAll(enquiryBasicsBuilder.convertEnquiryBasics(depositArrangement, true, channelId, userIdAuthor, null, null).build());
        financeEnquiryBasic.setEnquiryBasicExtras(enquiryBasicExtras);
        JAXBElement<FinanceEnquiryBasicType> financeEnquiryBasicTypeJAXBElement = new JAXBElement(ENQUIRY_BASIC_QNAME, FinanceEnquiryBasicType.class, financeEnquiryBasic);
        EnquiryBasicsType enquiryBasicsType = new EnquiryBasicsType();
        enquiryBasicsType.getEnquiryBasic().add(financeEnquiryBasicTypeJAXBElement);
        FinanceEnquiryType financeEnquiryType = new FinanceEnquiryType();
        financeEnquiryType.setEnquiryUId(depositArrangement.getArrangementId());
        financeEnquiryType.setEnquiryDateTime(appDate);
        financeEnquiryType.setChannel(ChannelType.INTERNET);
        financeEnquiryType.setEnquiryBasics(enquiryBasicsType);
        financeEnquiryType.setEnquiryExtra(convertEnquiryExtra(depositArrangement));
        PartyBuilder partyBuilder = new PartyBuilder();
        financeEnquiryType.setParties(partyBuilder.createPartiesType(depositArrangement, retrieveLookUpValues(channelId), true));
        JAXBElement<FinanceEnquiryType> financeEnquiryTypeJAXBElement = new JAXBElement(ENQUIRY_QNAME, FinanceEnquiryType.class, financeEnquiryType);
        sourceData.getContent().add(financeEnquiryTypeJAXBElement);
        return sourceData;
    }

    private XMLGregorianCalendar getXmlGregorianDate(Date date) {
        try {
            return dateFactory.dateToXMLGregorianCalendar(date);
        } catch (DatatypeConfigurationException e) {
            LOGGER.info("Exception caught : ", e);
        }
        return null;
    }

    private DecisionType getAsmDecision(List<CustomerScore> customerScoreList) {
        if (customerScoreList != null && customerScoreList.size() >= 2 && customerScoreList.get(1) != null) {
            if (ActivateCommonConstant.AsmDecision.ACCEPT.equals(customerScoreList.get(1).getScoreResult())) {
                return DecisionType.ACCEPT;
            } else if (ActivateCommonConstant.AsmDecision.REFERRED.equals(customerScoreList.get(1).getScoreResult())) {
                return DecisionType.REFER;
            } else if (ActivateCommonConstant.AsmDecision.DECLINED.equals(customerScoreList.get(1).getScoreResult())) {
                return DecisionType.DECLINE;
            }
        }
        return null;
    }

    private DecisionDataType getDecisions(DepositArrangement depositArrangement) {
        DecisionDataType decisions = new DecisionDataType();
        DecisionDetailType decision = new DecisionDetailType();
        decision.setDecision(DecisionType.ACCEPT);
        decision.setDecisionSource(DecisionSourceType.MAIN);
        decision.setDecisionDateTime(getXmlGregorianDate(new Date()));
        DecisionDetailType asmDecision = new DecisionDetailType();
        asmDecision.setDecision(getAsmDecision(depositArrangement.getPrimaryInvolvedParty().getCustomerScore()));
        asmDecision.setDecisionSource(DecisionSourceType.CREDIT);
        asmDecision.setDecisionDateTime(getXmlGregorianDate(new Date()));
        decisions.getDecision().add(decision);
        decisions.getDecision().add(asmDecision);
        return decisions;
    }

    private EnquiryExtraType convertEnquiryExtra(DepositArrangement depositArrangement) {
        EnquiryExtraType enquiryExtra = new EnquiryExtraType();
        ElectronicType electronic = new ElectronicType();
        electronic.setIpAddress(getCustomerDeviceDetails(depositArrangement).getTrueIp());
        enquiryExtra.setElectronic(electronic);
        enquiryExtra.setServiceOptOut(new ServiceOptOutType());
        enquiryExtra.setBureauData(new BureauDataType());
        DeviceDetailsType deviceDetailsType = new DeviceDetailsType();
        DeviceDataBuilder deviceDataBuilder = new DeviceDataBuilder();
        deviceDetailsType.getThreatMetrixOrIovation().add(deviceDataBuilder.convertTmxData(getCustomerDeviceDetails(depositArrangement)).build());
        enquiryExtra.setDeviceDetails(deviceDetailsType);
        return enquiryExtra;
    }

    private CustomerDeviceDetails getCustomerDeviceDetails(DepositArrangement depositArrangement) {
        CustomerDeviceDetails customerDeviceDetails = new CustomerDeviceDetails();
        if (depositArrangement.getPrimaryInvolvedParty() != null && null != depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy()) {
            customerDeviceDetails = depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getCustomerDeviceDetails();
        }
        return customerDeviceDetails;
    }

    private Map<String, String> retrieveLookUpValues(String channelId) {
        List<String> groupCodes = new ArrayList<>();
        List<ReferenceDataLookUp> lookUpValues;
        Map<String, String> countryCodeMap = new HashMap<>();
        groupCodes.add(ISO_COUNTRY_CODE);
        lookUpValues = lookUpValueRetriever.getLookUpValues(groupCodes, channelId);
        for (ReferenceDataLookUp lookUp : lookUpValues) {
            if (lookUp.getGroupCode().equals(ISO_COUNTRY_CODE) && lookUp.getLookupText() != null && lookUp.getLookupValueDesc() != null) {
                countryCodeMap.put(lookUp.getLookupText(), lookUp.getLookupValueDesc());
            }
        }
        return countryCodeMap;
    }
}