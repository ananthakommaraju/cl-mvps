package com.lloydsbanking.salsa.offer.identify.utility;

import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.StructuredAddress;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

public class DeliveryPointSuffixAnalyser {
    private static final Logger LOGGER = Logger.getLogger(DeliveryPointSuffixAnalyser.class);

    public boolean isDeliveryPointSuffixPresent(List<PostalAddress> postalAddressList) {
        LOGGER.info("Entering isDeliveryPointSuffixPresent with Postal Address size: " + postalAddressList.size());
        String pointSuffix = findPointSuffixForPostalAddress(postalAddressList);
        if (!StringUtils.isEmpty(pointSuffix)) {
            LOGGER.info("DeliveryPointSuffix is present");
            return true;
        }
        LOGGER.info("DeliveryPointSuffix is not present");
        return false;
    }

    private String findPointSuffixForPostalAddress(List<PostalAddress> postalAddressList) {
        String pointSuffix = null;
        if (postalAddressList != null) {
            for (PostalAddress postalAddress : postalAddressList) {
                if (isCurrentAddress(postalAddress.getStatusCode())) {
                    if (null != postalAddress.isIsPAFFormat() && postalAddress.isIsPAFFormat()) {
                        pointSuffix = getDeliveryPointSuffixForStructuredAddress(postalAddress.getStructuredAddress());
                    } else {
                        pointSuffix = getDeliveryPointSuffixForUnstructuredAddress(postalAddress.getUnstructuredAddress());
                    }
                }
            }
        }
        return pointSuffix;
    }

    private String getDeliveryPointSuffixForUnstructuredAddress(UnstructuredAddress unstructuredAddress) {
        if (unstructuredAddress != null) {
            return unstructuredAddress.getPointSuffix();
        }
        return null;
    }

    private String getDeliveryPointSuffixForStructuredAddress(StructuredAddress structuredAddress) {
        if (structuredAddress != null) {
            return structuredAddress.getPointSuffix();
        }
        return null;
    }

    private boolean isCurrentAddress(String statusCode) {
        if ("CURRENT".equalsIgnoreCase(statusCode)) {
            return true;
        }
        return false;
    }

}
