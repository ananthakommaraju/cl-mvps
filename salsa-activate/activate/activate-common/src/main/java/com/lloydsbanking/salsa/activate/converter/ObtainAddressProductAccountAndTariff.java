package com.lloydsbanking.salsa.activate.converter;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.soap.fs.account.StAddress;
import com.lloydsbanking.salsa.soap.fs.account.StAddressLine;
import lib_sim_bo.businessobjects.StructuredAddress;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ObtainAddressProductAccountAndTariff {
    public static final String TSB_SORT_CODE = "87";

    public static final int POS_OF_PROD_ID = 0;

    public static final int LENGTH_OF_PROD_ID = 5;

    public static final int POS_OF_ACC_TYPE = 6;

    public static final int ACC_TYPE_LENGTH = 2;

    public static final int FIRST_YEAR_OF_STUDY = 1;

    public static final int SECOND_YEAR_OF_STUDY = 2;

    public static final int THIRD_YEAR_OF_STUDY = 3;

    public static final int FOURTH_YEAR_OF_STUDY = 4;

    public static final int FIFTH_YEAR_OF_STUDY = 5;

    public static final int SIXTH_YEAR_OF_STUDY = 6;

    public static final int MAX_ADDRESS_LINES = 4;

    public static final int POST_CODE_MAX_LENGTH = 8;

    public static final int POST_CODE_SEPARATION_LENGTH = 3;


    public String getAccountTariff(int currentYearOfStudy) {
        String tariffType;
        switch (currentYearOfStudy) {

            case FIRST_YEAR_OF_STUDY:
                tariffType = ActivateCommonConstant.TariffTypes.ONE_YEAR_TARIFF;
                break;
            case SECOND_YEAR_OF_STUDY:
                tariffType = ActivateCommonConstant.TariffTypes.SECOND_YEAR_TARIFF;
                break;
            case THIRD_YEAR_OF_STUDY:
                tariffType = ActivateCommonConstant.TariffTypes.THIRD_YEAR_TARIFF;
                break;
            case FOURTH_YEAR_OF_STUDY:
                tariffType = ActivateCommonConstant.TariffTypes.FOURTH_YEAR_TARIFF;
                break;
            case FIFTH_YEAR_OF_STUDY:
                tariffType = ActivateCommonConstant.TariffTypes.FIFTH_YEAR_TARIFF;
                break;
            case SIXTH_YEAR_OF_STUDY:
                tariffType = ActivateCommonConstant.TariffTypes.SIXTH_YEAR_TARIFF;
                break;
            default:
                tariffType = ActivateCommonConstant.TariffTypes.DEFAULT_TARIFF;
        }
        return (tariffType);

    }

    public StAddress getStructureAddress(StructuredAddress postalStructureAddress) {
        StAddress b765StructureAddress = new StAddress();
        List<String> addresses = new ArrayList<>();
        addresses.add(postalStructureAddress.getOrganisation());
        addresses.add(postalStructureAddress.getSubBuilding());
        addresses.add(postalStructureAddress.getBuilding());
        addresses.add(postalStructureAddress.getBuildingNumber());
        addresses.add(postalStructureAddress.getHouseNumber());
        addresses.add(postalStructureAddress.getHouseName());
        if (!postalStructureAddress.getAddressLinePAFData().isEmpty()) {
            addresses.add(postalStructureAddress.getAddressLinePAFData().get(0));
        }
        addresses.add(postalStructureAddress.getPostTown());
        addresses.add(postalStructureAddress.getCountry());
        int count = 0;
        for (String address : addresses) {
            if (address != null && count < MAX_ADDRESS_LINES) {
                StAddressLine stAddressLine = new StAddressLine();
                stAddressLine.setAddressline(address);
                b765StructureAddress.getAstaddressline().add(stAddressLine);
                count++;
            }
        }
        int postCodeLength = postalStructureAddress.getPostCodeOut().length() + postalStructureAddress.getPostCodeIn().length();
        b765StructureAddress.setPostcode(postalStructureAddress.getPostCodeOut() + getSpaceString(postCodeLength) + postalStructureAddress.getPostCodeIn());
        return b765StructureAddress;
    }

    public StAddress getUnstructuredAddress(UnstructuredAddress unstructuredAddress) {
        StAddress b765StructureAddress = new StAddress();
        List<String> addresses = new ArrayList<>();
        addresses.add(unstructuredAddress.getAddressLine1());
        addresses.add(unstructuredAddress.getAddressLine2());
        addresses.add(unstructuredAddress.getAddressLine3());
        addresses.add(unstructuredAddress.getAddressLine4());
        for (String address : addresses) {
            if (address != null) {
                StAddressLine stAddressLine = new StAddressLine();
                stAddressLine.setAddressline(address);
                b765StructureAddress.getAstaddressline().add(stAddressLine);
            }
        }
        b765StructureAddress.setPostcode(getFormattedPostCode(unstructuredAddress.getPostCode()));
        return b765StructureAddress;
    }


    private String getSpaceString(int postCodeSize) {
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < POST_CODE_MAX_LENGTH - postCodeSize; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    private String getFormattedPostCode(String postCode) {
        String finalPostCode = postCode;
        if (!StringUtils.isEmpty(postCode)) {
            if (postCode.contains(" ")) {
                String postcodeOut = postCode.substring(0, postCode.indexOf(" "));
                String postcodeIn = postCode.substring(postCode.lastIndexOf(" ") + 1, postCode.length());
                int postCodeLength = postcodeOut.length() + postcodeIn.length();
                finalPostCode = postcodeOut + getSpaceString(postCodeLength) + postcodeIn;
            } else if (!postCode.contains(" ")) {
                String postcodeLastThree = postCode.substring(postCode.length() - POST_CODE_SEPARATION_LENGTH, postCode.length());
                String postcodeExtracted = postCode.substring(0, postCode.length() - POST_CODE_SEPARATION_LENGTH);
                finalPostCode = postcodeExtracted + " " + postcodeLastThree;
            }
        }
        return finalPostCode;
    }

    public List<String> getProdAcc(String sortCode, String extSysProdIdentifier, String arrangementType) {
        String accType = extSysProdIdentifier.substring(POS_OF_ACC_TYPE, POS_OF_ACC_TYPE + ACC_TYPE_LENGTH - 1);
        String prodNum = extSysProdIdentifier.substring(POS_OF_PROD_ID, POS_OF_PROD_ID + LENGTH_OF_PROD_ID - 1);

        if (!ArrangementType.SAVINGS.getValue().equalsIgnoreCase(arrangementType) && sortCode != null && sortCode.startsWith(TSB_SORT_CODE)) {
            if (ActivateCommonConstant.ProdIdentifier.CLASSIC_ACC_PROD_ID_OLD.equals(prodNum)) {
                prodNum = ActivateCommonConstant.ProdIdentifier.CLASSIC_ACC_PROD_NUMBER_OLD;
            } else if (ActivateCommonConstant.ProdIdentifier.CLASSIC_ACC_PROD_ID_NEW.equals(prodNum)) {
                prodNum = ActivateCommonConstant.ProdIdentifier.CLASSIC_ACC_PROD_NUMBER_NEW;
            }
        }

        return Arrays.asList(accType, prodNum);
    }

}