package com.lloydsbanking.salsa.opacc.client;

import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.StructuredAddress;
import lib_sim_bo.businessobjects.UnstructuredAddress;

public class PostalAddressBuilder {
    PostalAddress postalAddress;

    public PostalAddressBuilder() {
        postalAddress = new PostalAddress();
    }

    public PostalAddress build() {
        return postalAddress;
    }

    public PostalAddressBuilder statusCode(String statusCode) {
        postalAddress.setStatusCode(statusCode);
        return this;
    }

    public PostalAddressBuilder isPAFFormat(boolean isPAFFormat) {
        postalAddress.setIsPAFFormat(isPAFFormat);
        return this;
    }

    public PostalAddressBuilder durationOfStay(String durationOfStay) {
        postalAddress.setDurationofStay(durationOfStay);
        return this;
    }

    public PostalAddressBuilder structuredAddress(StructuredAddress structuredAddress) {
        postalAddress.setStructuredAddress(structuredAddress);
        return this;
    }

    public PostalAddressBuilder unstructuredAddress(UnstructuredAddress unstructuredAddress) {
        postalAddress.setUnstructuredAddress(unstructuredAddress);
        return this;
    }

    public PostalAddressBuilder isBFPOAddressBuilder(boolean isBFPOAddressBuilder) {
        postalAddress.setIsBFPOAddress(isBFPOAddressBuilder);
        return this;
    }
}
