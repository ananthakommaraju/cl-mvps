package com.lloydsbanking.salsa.opapca.service.utility;

import com.lloydsbanking.salsa.downstream.pam.service.constant.PamConstant;
import lib_sim_bo.businessobjects.CustomerDeviceDetails;
import lib_sim_bo.businessobjects.CustomerScore;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SiraHelper {
    private static final Logger LOGGER = Logger.getLogger(SiraHelper.class);

    public String toSerializedString(CustomerDeviceDetails customerDeviceDetails) {
        if (null != customerDeviceDetails) {
            ByteArrayOutputStream customerDeviceDetailsToSerializedString = new ByteArrayOutputStream();
            try {
                JAXBContext jc = JAXBContext.newInstance(String.class, CustomerDeviceDetails.class);
                JAXBIntrospector introspector = jc.createJAXBIntrospector();
                Marshaller marshaller = jc.createMarshaller();
                if (null == introspector.getElementName(customerDeviceDetails)) {
                    JAXBElement jaxbElement = new JAXBElement(new QName("CustomerDeviceDetails"), Object.class, customerDeviceDetails);
                    marshaller.marshal(jaxbElement, customerDeviceDetailsToSerializedString);
                } else {
                    marshaller.marshal(customerDeviceDetails, customerDeviceDetailsToSerializedString);
                }
            } catch (JAXBException exception) {
                LOGGER.error("Exception occurred in Conversion. ;", exception);
            }
            String deviceDataString = null;
            try {
                deviceDataString = new String(customerDeviceDetailsToSerializedString.toByteArray(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LOGGER.info("CustomerDeviceDetails :toSerializedString() Exception Occurred during conversion of device data to String" + e);
            }
            return deviceDataString;
        }
        return null;
    }

    public List<String> calculateEidvAndAsmScore(List<CustomerScore> customerScoreList) {
        List<String> scoreResultString = new ArrayList<>();
        for (CustomerScore customerScore : customerScoreList) {
            if (null != customerScore.getScoreResult()) {
                if ("EIDV".equalsIgnoreCase(customerScore.getAssessmentType())) {
                    scoreResultString.add(customerScore.getScoreResult());
                }
                if (PamConstant.ASM_ASSESSMENT_TYPE.equalsIgnoreCase(customerScore.getAssessmentType())) {
                    scoreResultString.add(customerScore.getScoreResult());
                }
            }
        }
        return scoreResultString;
    }

}
