package com.lloydsbanking.salsa.offer.identify.utility;

import com.lloydsbanking.salsa.constant.CustomerSegment;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import lib_sim_bo.businessobjects.Product;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.List;

public class CustomerUtility {

    public static final BigInteger RANGE_MINIMUM = BigInteger.valueOf(9120996000000000000L);

    public static final BigInteger RANGE_MAXIMUM = BigInteger.valueOf(9120999999999999999L);

    private static final String CURRENT_ACCOUNT_GROUP_ID = "1";

    private static final short VISION_PLUS_SOURCE_SYSTEM_IDENTIFIER = 13;

    private static final short CBS_SOURCE_SYSTEM_IDENTIFIER = 4;


    private LookupDataRetriever offerLookupDataRetriever;

    private static final Logger LOGGER = Logger.getLogger(CustomerUtility.class);

    public String getCustomerSegment(List<Product> productsList) {
        LOGGER.info("Entering getCustomerSegment with product list size: " + productsList.size());
        String custSegment = CustomerSegment.NON_FRANCHISED.getValue();

        if (productsList.isEmpty()) {
            LOGGER.info("No Valid Product Holdings");
            custSegment = CustomerSegment.NON_FRANCHISED.getValue();
        } else {
            LOGGER.info("Setting Customer Segment");
            for (Product product : productsList) {
                if (CURRENT_ACCOUNT_GROUP_ID.equals(product.getProductIdentifier())) {
                    custSegment = CustomerSegment.FRANCHISED.getValue();
                    break;
                } else {
                    custSegment = CustomerSegment.NON_ALIGNED.getValue();
                }
            }
            LOGGER.info("Exiting getCustomerSegment, Customer Segment: " + custSegment);
            return custSegment;
        }
        LOGGER.info("Exiting getCustomerSegment, Customer Segment: " + custSegment);
        return custSegment;
    }

    public String getCBSCustomerNumber(List<Product> productsList) {
        LOGGER.info("Entering getCBSCustomerNumber with product list size: " + productsList.size());
        String cbsCustomerNumber = null;

        if (!productsList.isEmpty()) {
            LOGGER.info("Setting CBS Customer Number");
            for (Product product : productsList) {
                if (!CollectionUtils.isEmpty(product.getExternalSystemProductIdentifier() ) &&
                        !StringUtils.isEmpty(product.getExternalSystemProductIdentifier().get(0).getSystemCode()) &&
                        CBS_SOURCE_SYSTEM_IDENTIFIER == Short.parseShort(product.getExternalSystemProductIdentifier().get(0).getSystemCode())) {
                    if (CURRENT_ACCOUNT_GROUP_ID.equals(product.getProductIdentifier())) {
                        cbsCustomerNumber = product.getExtPartyIdTx();
                        break;
                    } else {
                        cbsCustomerNumber = product.getExtPartyIdTx();
                    }
                }
            }
        }
        LOGGER.info("Exiting getCBSCustomerNumber, CBS Customer Number: " + cbsCustomerNumber);
        return cbsCustomerNumber;
    }

    // added for second card & Trade initiative
    public String getFDICustomerID(List<Product> productsList) {
        LOGGER.info("Entering getFDICustomerID with product list size: " + productsList.size());
        String fdiCustomerID = null;
        if (!productsList.isEmpty()) {
            LOGGER.info("Setting FDI Customer ID");
            for (Product product : productsList) {
                if (!CollectionUtils.isEmpty(product.getExternalSystemProductIdentifier() ) &&
                        !StringUtils.isEmpty(product.getExternalSystemProductIdentifier().get(0).getSystemCode())  &&
                        VISION_PLUS_SOURCE_SYSTEM_IDENTIFIER == Short.parseShort(product.getExternalSystemProductIdentifier().get(0).getSystemCode())) {
                    String visionPlusID = product.getExtPartyIdTx();
                    LOGGER.info("Vision Plus ID: " + visionPlusID);
                    visionPlusID = visionPlusID.trim();
                    if (checkCustomerIdRange(visionPlusID)) {
                        fdiCustomerID = visionPlusID;
                        break;
                    } else {
                        LOGGER.info("Out of Range FDI Customer ID: " + fdiCustomerID);
                    }
                }
            }
        }
        LOGGER.info("Exiting getFDICustomerID, FDI Customer ID: " + fdiCustomerID);
        return fdiCustomerID;
    }

    public boolean checkCustomerIdRange(String visionPlusID) {
        boolean outsideRange = true;
        LOGGER.info("Checking Customer ID range 9120996000000000000 to 9120999999999999999: " + visionPlusID);
        BigInteger custId;

        if (visionPlusID != null && visionPlusID.trim().length() > 0) {
            custId = new BigInteger(visionPlusID);
            int comparedToMinimumRange = custId.compareTo(RANGE_MINIMUM);
            int comparedToMaximumRange = custId.compareTo(RANGE_MAXIMUM);
            LOGGER.info("compare with MIN range: " + comparedToMinimumRange);
            LOGGER.info("compare with MAX range: " + comparedToMaximumRange);

            if (isEqualToRange(comparedToMinimumRange, comparedToMaximumRange)) {
                outsideRange = false;
                LOGGER.info("Equal to range: " + outsideRange);
            } else if (isWithinRange(comparedToMinimumRange, comparedToMaximumRange)) {
                outsideRange = false;
                LOGGER.info("Inside the range: " + outsideRange);
            } else if (isOutsideRange(comparedToMinimumRange, comparedToMaximumRange)) {
                LOGGER.info("Outside the range: " + outsideRange);
            }
        } else {
            outsideRange = false;
            LOGGER.info("Customer ID is NULL or Blank");
        }
        return outsideRange;
    }

    private boolean isOutsideRange(int comparedToMimimumRange, int comparedToMaximumRange) {
        return comparedToMimimumRange < 0 || comparedToMaximumRange > 0;
    }

    private boolean isWithinRange(int comparedToMimimumRange, int comparedToMaximumRange) {
        return comparedToMimimumRange > 0 && comparedToMaximumRange < 0;
    }

    private boolean isEqualToRange(int comparedToMimimumRange, int comparedToMaximumRange) {
        return comparedToMimimumRange == 0 || comparedToMaximumRange == 0;
    }
}
