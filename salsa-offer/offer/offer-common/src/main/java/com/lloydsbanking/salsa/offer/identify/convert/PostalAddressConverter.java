package com.lloydsbanking.salsa.offer.identify.convert;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.AddressData;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.AddressLinePaf;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.StructuredAddress;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class PostalAddressConverter {
    private static final Logger LOGGER = Logger.getLogger(PostalAddressConverter.class);

    @Autowired
    DateFactory dateFactory;

    private static final String STATUS_CODE_CURRENT = "001";

    public PostalAddress getPostalAddress(AddressData addressData) throws DatatypeConfigurationException, ParseException {
        PostalAddress postalAddress = new PostalAddress();
        if (null != addressData) {
            postalAddress.setEvidenceTypeCode(addressData.getAddressTypeCd());
            if (STATUS_CODE_CURRENT.equals(addressData.getAddressStatusCd())) {
                postalAddress.setStatusCode("CURRENT");
            }
            if (!StringUtils.isEmpty(addressData.getAmdEffDt())) {
                postalAddress.setDurationofStay(getDurationOfStay(addressData.getAmdEffDt()));
                postalAddress.setEffectiveFrom(getEffectiveFrom(addressData.getAmdEffDt()));
            }
            if (null != addressData.getStructuredAddress()) {
                postalAddress.setStructuredAddress(new StructuredAddress());
                postalAddress.setIsPAFFormat(true);
                setStructuredAddress(addressData.getStructuredAddress(), postalAddress.getStructuredAddress());
                setUnstructuredAddressIfStructuredExists(addressData, postalAddress);
            } else {
                setUnstructuredAddressIfStructuredDoesNotExist(addressData, postalAddress);
            }
        }
        return postalAddress;
    }

    private void setUnstructuredAddressIfStructuredDoesNotExist(AddressData addressData, PostalAddress postalAddress) {
        if (null != addressData.getUnstructuredAddress()) {
            postalAddress.setUnstructuredAddress(new UnstructuredAddress());

            postalAddress.setIsPAFFormat(false);
            postalAddress.getUnstructuredAddress().setAddressLine1(addressData.getUnstructuredAddress().getAddressLine1Tx());
            postalAddress.getUnstructuredAddress().setAddressLine2(addressData.getUnstructuredAddress().getAddressLine2Tx());
            postalAddress.getUnstructuredAddress().setAddressLine3(addressData.getUnstructuredAddress().getAddressLine3Tx());
            postalAddress.getUnstructuredAddress().setAddressLine4(addressData.getUnstructuredAddress().getAddressLine4Tx());
            postalAddress.getUnstructuredAddress().setAddressLine5(addressData.getUnstructuredAddress().getAddressLine5Tx());
            postalAddress.getUnstructuredAddress().setAddressLine6(addressData.getUnstructuredAddress().getAddressLine6Tx());
            postalAddress.getUnstructuredAddress().setAddressLine7(addressData.getUnstructuredAddress().getAddressLine7Tx());
            postalAddress.getUnstructuredAddress().setAddressLine8("United Kingdom");
            postalAddress.getUnstructuredAddress().setPostCode(addressData.getUnstructuredAddress().getPostCd());

        }
    }

    private void setUnstructuredAddressIfStructuredExists(AddressData addressData, PostalAddress postalAddress) {
        if (null != addressData.getUnstructuredAddress()) {
            postalAddress.setUnstructuredAddress(new UnstructuredAddress());
            postalAddress.getUnstructuredAddress().setAddressLine1(addressData.getUnstructuredAddress().getAddressLine1Tx());
            postalAddress.getUnstructuredAddress().setAddressLine2(addressData.getUnstructuredAddress().getAddressLine2Tx());
            postalAddress.getUnstructuredAddress().setAddressLine3(addressData.getUnstructuredAddress().getAddressLine3Tx());
            postalAddress.getUnstructuredAddress().setAddressLine4(addressData.getUnstructuredAddress().getAddressLine4Tx());
            postalAddress.getUnstructuredAddress().setAddressLine8("United Kingdom");
            postalAddress.getUnstructuredAddress().setPostCode(addressData.getUnstructuredAddress().getPostCd());
        }

    }

    private Date getDateInGMTFormat(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.parse(dateFormat.format(dateFormat.parse(dateFactory.getDatePattern(date, "-"))));
    }

    private XMLGregorianCalendar getEffectiveFrom(String date) throws ParseException, DatatypeConfigurationException {
        try {
            return dateFactory.dateToXMLGregorianCalendar(getDateInGMTFormat(date));
        } catch (ParseException e) {
            LOGGER.info("Exception caught : ", e);
            throw e;
        } catch (DatatypeConfigurationException e) {
            LOGGER.info("Exception caught : ", e);
            throw e;
        }
    }

    private String getDurationOfStay(String date) throws ParseException {
        long duration = 0;
        try {
            duration = dateFactory.differenceInDays(getDateInGMTFormat(date), new Date());
        } catch (ParseException e) {
            LOGGER.info("Exception caught : ", e);
            throw e;
        }
        return dateFactory.calculateDurationOfStay(duration);

    }


    private void setStructuredAddress(com.lloydsbanking.salsa.soap.ocis.f061.objects.StructuredAddress structuredAddress, StructuredAddress postalAddressStructuredAddress) {
        postalAddressStructuredAddress.setOrganisation(structuredAddress.getOrganisationNm());
        postalAddressStructuredAddress.setSubBuilding(structuredAddress.getSubBuildingNm());
        if (!StringUtils.isEmpty(structuredAddress.getBuildingNm())) {
            postalAddressStructuredAddress.setBuilding(structuredAddress.getBuildingNm());
        } else if (!CollectionUtils.isEmpty(structuredAddress.getAddressLinePaf()) && StringUtils.isEmpty(structuredAddress.getBuildingNo())) {
            postalAddressStructuredAddress.setBuilding(structuredAddress.getAddressLinePaf().get(0).getAddressLinePafTx());
        }

        postalAddressStructuredAddress.setBuildingNumber(structuredAddress.getBuildingNo());
        postalAddressStructuredAddress.setDistrict(structuredAddress.getAddressDistrictNm());
        postalAddressStructuredAddress.setPostTown(structuredAddress.getAddressPostTownNm());
        postalAddressStructuredAddress.setCountry("UK");
        postalAddressStructuredAddress.setPostCodeOut(structuredAddress.getOutPostCd());
        postalAddressStructuredAddress.setPostCodeIn(structuredAddress.getInPostCd());
        postalAddressStructuredAddress.setPointSuffix(structuredAddress.getDelivPointSuffixCd());
        postalAddressStructuredAddress.setCounty(structuredAddress.getAddressCountyNm());
        if (null != structuredAddress.getAddressLinePaf()) {
            for (AddressLinePaf addressLine : structuredAddress.getAddressLinePaf()) {
                postalAddressStructuredAddress.getAddressLinePAFData().add(addressLine.getAddressLinePafTx());
            }
        }
    }
}