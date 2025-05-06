package com.lloydsbanking.salsa.apapca.service.fulfil.converter;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.account.client.b276.RetrieveAccProcessOverdraftRequestBuilder;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.StructuredAddress;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class AddressDetails {
    private static final Logger LOGGER = Logger.getLogger(AddressDetails.class);
    private static final int POSITION_OF_YEAR = 0;
    private static final int LENGTH_OF_XX = 2;
    private static final int POSITION_OF_MONTH = 2;
    private static final String POST_CODE_DEFAULT = "0";
    private static final String DATE_ADDRESS_EFFECT_DEFAULT = "20100708";

    public void setAddressDetails(List<PostalAddress> postalAddressList, RetrieveAccProcessOverdraftRequestBuilder builder) {
        setDefaultAddressDetails(builder);
        for (PostalAddress postalAddress : postalAddressList) {
            if (ActivateCommonConstant.ApaPcaServiceConstants.ADDRESS_TYPE_CURRENT.equalsIgnoreCase(postalAddress.getStatusCode()) ||
                    ActivateCommonConstant.ApaPcaServiceConstants.ADDRESS_TYPE_PREVIOUS.equalsIgnoreCase(postalAddress.getStatusCode())) {
                String durationOfStay = getConvertedDurationOfStay(postalAddress.getDurationofStay());
                XMLGregorianCalendar dateAddressEffect = stringToXMLGregorianCalendar(durationOfStay, FastDateFormat.getInstance("yyyyMMdd"));
                Boolean isbPafAddressCurrent = postalAddress.isIsPAFFormat();
                List<String> addressLineCurrent;
                String postCode;
                if (postalAddress.isIsPAFFormat()) {
                    addressLineCurrent = getStructuredAddressLineList(postalAddress.getStructuredAddress());
                    postCode = postalAddress.getStructuredAddress().getPostCodeIn() + postalAddress.getStructuredAddress().getPostCodeOut();
                } else {
                    addressLineCurrent = getUnstructuredAddressLineList(postalAddress.getUnstructuredAddress());
                    postCode = postalAddress.getUnstructuredAddress().getPostCode();
                }

                if (ActivateCommonConstant.ApaPcaServiceConstants.ADDRESS_TYPE_CURRENT.equalsIgnoreCase(postalAddress.getStatusCode())) {
                    builder.addressDetails(addressLineCurrent, postCode, isbPafAddressCurrent, dateAddressEffect);
                } else if (ActivateCommonConstant.ApaPcaServiceConstants.ADDRESS_TYPE_PREVIOUS.equalsIgnoreCase(postalAddress.getStatusCode())) {
                    builder.addressPreviousDetails(addressLineCurrent, postCode, isbPafAddressCurrent, dateAddressEffect);
                }
            }
        }
    }


    private void setDefaultAddressDetails(RetrieveAccProcessOverdraftRequestBuilder builder) {
        XMLGregorianCalendar dateAddressEffect = stringToXMLGregorianCalendar(DATE_ADDRESS_EFFECT_DEFAULT, FastDateFormat.getInstance("yyyyMMdd"));
        builder.addressDetails(new ArrayList<String>(), POST_CODE_DEFAULT, false, dateAddressEffect)
                .addressPreviousDetails(new ArrayList<String>(), POST_CODE_DEFAULT, false, dateAddressEffect);

    }

    private List<String> getStructuredAddressLineList(StructuredAddress structuredAddress) {
        List<String> addressLineCurrent = new ArrayList<>();
        addAddressLineIfPresent(addressLineCurrent, structuredAddress.getBuildingNumber());
        addAddressLineIfPresent(addressLineCurrent, structuredAddress.getBuilding());
        addAddressLineIfPresent(addressLineCurrent, structuredAddress.getSubBuilding());
        addAddressLineIfPresent(addressLineCurrent, structuredAddress.getStreet());
        addAddressLineIfPresent(addressLineCurrent, structuredAddress.getDistrict());
        addAddressLineIfPresent(addressLineCurrent, structuredAddress.getPostTown());
        addAddressLineIfPresent(addressLineCurrent, structuredAddress.getCountry());
        return addressLineCurrent;
    }

    private List<String> getUnstructuredAddressLineList(UnstructuredAddress unstructuredAddress) {
        List<String> addressLineCurrent = new ArrayList<>();
        addAddressLineIfPresent(addressLineCurrent, unstructuredAddress.getAddressLine1());
        addAddressLineIfPresent(addressLineCurrent, unstructuredAddress.getAddressLine2());
        addAddressLineIfPresent(addressLineCurrent, unstructuredAddress.getAddressLine3());
        addAddressLineIfPresent(addressLineCurrent, unstructuredAddress.getAddressLine4());
        addAddressLineIfPresent(addressLineCurrent, unstructuredAddress.getAddressLine5());
        addAddressLineIfPresent(addressLineCurrent, unstructuredAddress.getAddressLine6());
        addAddressLineIfPresent(addressLineCurrent, unstructuredAddress.getAddressLine7());
        return addressLineCurrent;
    }

    private String getConvertedDurationOfStay(String durationOfStay) {
        String year = durationOfStay.substring(POSITION_OF_YEAR, POSITION_OF_YEAR + LENGTH_OF_XX);
        String month = durationOfStay.substring(POSITION_OF_MONTH, POSITION_OF_MONTH + LENGTH_OF_XX);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -Integer.parseInt(month));
        cal.add(Calendar.YEAR, -Integer.parseInt(year));
        return FastDateFormat.getInstance("yyyyMMdd").format(cal);
    }

    private void addAddressLineIfPresent(List<String> addressList, String addressLine) {
        if (addressLine != null) {
            addressList.add(addressLine);
        }
    }

    private XMLGregorianCalendar stringToXMLGregorianCalendar(String datetime, FastDateFormat fastDateFormat) {
        DateFactory dateFactory = new DateFactory();
        Date date = null;
        try {
            date = fastDateFormat.parse(datetime);
            return dateFactory.dateToXMLGregorianCalendar(date);
        } catch (ParseException e) {
            LOGGER.info("Parse Exception in B276: ", e);
        } catch (DatatypeConfigurationException e) {
            LOGGER.info("DataTypeConfigurationException Exception in B276 : ", e);
        }
        return null;
    }

}