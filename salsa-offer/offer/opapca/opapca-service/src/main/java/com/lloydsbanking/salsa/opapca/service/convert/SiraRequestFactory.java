package com.lloydsbanking.salsa.opapca.service.convert;


import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.sira.client.DeviceDataBuilder;
import com.lloydsbanking.salsa.downstream.sira.client.EnquiryBasicsBuilder;
import com.lloydsbanking.salsa.downstream.sira.client.PartyBuilder;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.synectics_solutions.sira.schemas.realtime.base.v2_0.device.DeviceDetailsType;
import com.synectics_solutions.sira.schemas.realtime.base.v2_0.enquiry.*;
import com.synectics_solutions.sira.schemas.realtime.base.v2_0.enquirybasic.AdditionalDataType;
import com.synectics_solutions.sira.schemas.realtime.finance.v2_0.financeenquiry.FinanceEnquiryType;
import com.synectics_solutions.sira.schemas.realtime.finance.v2_0.financeenquirybasic.FinanceEnquiryBasicType;
import com.synectics_solutions.sira.schemas.realtime.variant.v2_0.datalibrary.ChannelType;
import com.synectics_solutions.ws.webservices.dataservices.v2.servicesaccess.Source;
import lib_sim_bo.businessobjects.CustomerDeviceDetails;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.util.*;

@Component
public class SiraRequestFactory {

    private static final Logger LOGGER = Logger.getLogger(SiraRequestFactory.class);
    private static final String PRODUCT_CODE = "CURRENT ACCOUNT";
    private static final String ISO_COUNTRY_CODE = "ISO_COUNTRY_CODE";
    private static final QName ENQUIRY_BASIC_QNAME = new QName("http://www.synectics-solutions.com/sira/schemas/realtime/finance/v2.0/FinanceEnquiryBasic.xsd", "EnquiryBasic");
    private static final QName ENQUIRY_QNAME = new QName("http://www.synectics-solutions.com/sira/schemas/realtime/finance/v2.0/FinanceEnquiry.xsd", "Enquiry");
    private static final String PRIMARY_PRODUCT_INDICATOR = "Y";

    @Autowired
    DateFactory dateFactory;

    @Autowired
    LookupDataRetriever lookupDataRetriever;

    @Autowired
    HeaderRetriever headerRetriever;

    public Source convert(DepositArrangement depositArrangement, RequestHeader requestHeader, Date applicationDate) {
        Source source = new Source();
        source.setSourceMessageId(String.valueOf(UUID.randomUUID()));
        source.setSourceDataId(depositArrangement.getArrangementId());
        String userIdAuthor = headerRetriever.getBapiInformationHeader(requestHeader) != null && headerRetriever.getBapiInformationHeader(requestHeader).getBAPIHeader() != null ? headerRetriever.getBapiInformationHeader(requestHeader).getBAPIHeader().getUseridAuthor() : null;
        source.setSourceData(convertSourceData(depositArrangement, requestHeader.getChannelId(), userIdAuthor, applicationDate));
        source.setSourceDataVersion(0);
        source.setDataLengthBytes(0);
        source.setSourceMessagePriority(0);
        source.setSourceMessageIssuedDateTime(getXmlGregorianDate(new Date()));
        return source;
    }

    private Source.SourceData convertSourceData(DepositArrangement depositArrangement, String channelId, String userIdAuthor, Date applicationDate) {
        Source.SourceData sourceData = new Source.SourceData();
        EnquiryBasicsBuilder enquiryBasicsBuilder = new EnquiryBasicsBuilder();
        AdditionalDataType enquiryBasicExtras = new AdditionalDataType();
        enquiryBasicExtras.getEnquiryBasicExtra().addAll(enquiryBasicsBuilder.convertEnquiryBasics(depositArrangement, false, channelId, userIdAuthor, applicationDate, null).build());

        FinanceEnquiryBasicType financeEnquiryBasic = new FinanceEnquiryBasicType();
        financeEnquiryBasic.setEnquiryBasicExtras(enquiryBasicExtras);

        financeEnquiryBasic.setProductCode(PRODUCT_CODE);
        financeEnquiryBasic.setPrimaryProductIndicator(PRIMARY_PRODUCT_INDICATOR);
        if (depositArrangement.isIsJointParty() != null && depositArrangement.isIsJointParty()) {
            financeEnquiryBasic.setMultipleApplicants("Y");
        } else {
            financeEnquiryBasic.setMultipleApplicants("N");
        }

        JAXBElement<FinanceEnquiryBasicType> financeEnquiryBasicTypeJAXBElement = new JAXBElement(ENQUIRY_BASIC_QNAME, FinanceEnquiryBasicType.class, financeEnquiryBasic);
        EnquiryBasicsType enquiryBasicsType = new EnquiryBasicsType();
        enquiryBasicsType.getEnquiryBasic().add(financeEnquiryBasicTypeJAXBElement);
        FinanceEnquiryType financeEnquiryType = new FinanceEnquiryType();
        financeEnquiryType.setEnquiryUId(depositArrangement.getArrangementId());
        financeEnquiryType.setEnquiryDateTime(setEnquiryDate());
        financeEnquiryType.setChannel(ChannelType.INTERNET);
        financeEnquiryType.setEnquiryBasics(enquiryBasicsType);
        financeEnquiryType.setEnquiryExtra(convertEnquiryExtra(depositArrangement));
        PartyBuilder partyBuilder = new PartyBuilder();
        financeEnquiryType.setParties(partyBuilder.createPartiesType
                (depositArrangement, retrieveLookUpValues(channelId), false));
        JAXBElement<FinanceEnquiryType> financeEnquiryTypeJAXBElement = new JAXBElement(ENQUIRY_QNAME, FinanceEnquiryType.class, financeEnquiryType);
        sourceData.getContent().add(financeEnquiryTypeJAXBElement);
        return sourceData;
    }

    private XMLGregorianCalendar setEnquiryDate() {
        try {
            Date date = new Date();
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

    private EnquiryExtraType convertEnquiryExtra(DepositArrangement depositArrangement) {
        EnquiryExtraType enquiryExtra = new EnquiryExtraType();
        DeviceDetailsType deviceDetailsType = new DeviceDetailsType();
        DeviceDataBuilder deviceDataBuilder = new DeviceDataBuilder();
        deviceDetailsType.getThreatMetrixOrIovation().add(deviceDataBuilder.convertTmxData(getCustomerDeviceDetails(depositArrangement)).build());

        ElectronicType electronic = new ElectronicType();
        electronic.setIpAddress(getCustomerDeviceDetails(depositArrangement).getTrueIp());
        enquiryExtra.setElectronic(electronic);
        enquiryExtra.setServiceOptOut(new ServiceOptOutType());
        enquiryExtra.setBureauData(new BureauDataType());
        enquiryExtra.setDeviceDetails(deviceDetailsType);
        return enquiryExtra;

    }

    private XMLGregorianCalendar getXmlGregorianDate(Date date) {
        try {
            return dateFactory.dateToXMLGregorianCalendar(date);
        } catch (DatatypeConfigurationException e) {
            LOGGER.info("Exception caught : ", e);
        }
        return null;
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
        try {
            lookUpValues = lookupDataRetriever.getLookupListFromChannelAndGroupCodeList(channelId, groupCodes);
            for (ReferenceDataLookUp lookUp : lookUpValues) {
                if (lookUp.getGroupCode().equals(ISO_COUNTRY_CODE) && lookUp.getLookupText() != null && lookUp.getLookupValueDesc() != null) {
                    countryCodeMap.put(lookUp.getLookupText(), lookUp.getLookupValueDesc());
                }
            }
        } catch (DataNotAvailableErrorMsg dataNotAvailableErrorMsg) {
            LOGGER.info("Data not available for the country" + dataNotAvailableErrorMsg);
        }
        return countryCodeMap;
    }
}
