package com.lloydsbanking.salsa.ppae.service.convert;


import com.lloydstsb.schema.enterprise.lcsm_involvedparty.wz.PostalAddress;
import com.lloydstsb.schema.enterprise.lcsm_involvedparty.wz.PostalAddressComponent;
import com.lloydstsb.schema.enterprise.lcsm_involvedparty.wz.PostalAddressComponentType;
import lib_sim_bo.businessobjects.StructuredAddress;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Component
public class PostalAddressHelper {

    private static final String ADDRESS_LINE_PREFIX = "Flat";
    private static final int ADDRESS_LINE_MAX_LENGTH = 50;
    private static final int ADDRESS_LINE_COUNT_2 = 2;
    private static final int ADDRESS_LINE_COUNT_3 = 3;
    private static final int TOWN_MAX_LENGTH = 25;

    public List<String> getAddressLines(UnstructuredAddress unstructuredAddress) {
        List<String> finalAddressLines = new ArrayList<>();

        List<String> addressLines = new ArrayList<>();

        if (unstructuredAddress != null) {
            addressLines.add(unstructuredAddress.getAddressLine2());
            addressLines.add(unstructuredAddress.getAddressLine3());
            addressLines.add(unstructuredAddress.getAddressLine4());
            addressLines.add(unstructuredAddress.getAddressLine5());
            addressLines.add(unstructuredAddress.getAddressLine7());
        }

        boolean isFlatAddressLineFound = false;

        for (int index = 0; index < addressLines.size() - 1; index++) {
            if (isFlatLineRequired(isFlatAddressLineFound, index) && null != addressLines.get(index) && null != addressLines.get(index + 1)) {
                finalAddressLines.add(getFormattedAddressLine(ADDRESS_LINE_PREFIX + addressLines.get(index) + " " + addressLines.get(index + 1), ADDRESS_LINE_MAX_LENGTH));
                isFlatAddressLineFound = true;
            } else if (null != addressLines.get(index)) {
                finalAddressLines.add(getFormattedAddressLine(addressLines.get(index), ADDRESS_LINE_MAX_LENGTH));
            }
        }
        return finalAddressLines;
    }

    public List<String> getAddressLines(List<String> addressLineList) {
        List<String> addressLines = new ArrayList<>();
        int count = 0;
        Iterator iterator = addressLineList.iterator();
        while (iterator.hasNext() && count < ADDRESS_LINE_COUNT_3) {
            addressLines.add((String) iterator.next());
        }
        return addressLines;
    }

    private boolean isFlatLineRequired(boolean isFlatAddressLineFound, int index) {
        return !isFlatAddressLineFound && index < ADDRESS_LINE_COUNT_2;
    }

    private String getFormattedAddressLine(String addressLine, int maxLength) {
        return null != addressLine && addressLine.length() > maxLength ? addressLine.substring(0, maxLength) : addressLine;
    }

    public PostalAddress createPostalAddressFromStructuredAddress(StructuredAddress structuredAddress) {
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.getAddressComponents().add(createPostalAddressComponent(PostalAddressComponentType.UNIT_NUMBER, Arrays.asList(structuredAddress.getBuildingNumber())));
        postalAddress.getAddressComponents().add(createPostalAddressComponent(PostalAddressComponentType.ADDRESS_LINES, getAddressLines(structuredAddress.getAddressLinePAFData())));
        postalAddress.getAddressComponents().add(createPostalAddressComponent(PostalAddressComponentType.TOWN, Arrays.asList(structuredAddress.getPostTown())));
        postalAddress.getAddressComponents().add(createPostalAddressComponent(PostalAddressComponentType.POSTAL_CODE, Arrays.asList(structuredAddress.getPostCodeOut() + structuredAddress.getPostCodeIn())));
        return postalAddress;
    }

    public PostalAddress createPostalAddressFromUnstructuredAddress(UnstructuredAddress unstructuredAddress) {
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.getAddressComponents().add(createPostalAddressComponent(PostalAddressComponentType.UNIT_NUMBER, Arrays.asList(unstructuredAddress.getAddressLine1())));
        postalAddress.getAddressComponents().add(createPostalAddressComponent(PostalAddressComponentType.ADDRESS_LINES, getAddressLines(unstructuredAddress)));
        String town = getFormattedAddressLine(unstructuredAddress.getAddressLine6(), TOWN_MAX_LENGTH);
        postalAddress.getAddressComponents().add(createPostalAddressComponent(PostalAddressComponentType.TOWN, Arrays.asList(town)));
        postalAddress.getAddressComponents().add(createPostalAddressComponent(PostalAddressComponentType.POSTAL_CODE, Arrays.asList(unstructuredAddress.getPostCode())));
        return postalAddress;
    }

    private PostalAddressComponent createPostalAddressComponent(PostalAddressComponentType postalAddressComponentType, List<String> valueList) {
        PostalAddressComponent postalAddressComponent = new PostalAddressComponent();
        postalAddressComponent.setType(postalAddressComponentType);
        postalAddressComponent.getValue().addAll(valueList);
        return postalAddressComponent;
    }
}
