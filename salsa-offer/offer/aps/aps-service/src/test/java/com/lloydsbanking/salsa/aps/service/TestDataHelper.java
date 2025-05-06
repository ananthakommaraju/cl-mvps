package com.lloydsbanking.salsa.aps.service;


import com.lloydsbanking.salsa.header.gmo.RequestHeaderBuilder;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.InstructionDetails;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductOffer;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.messages.AdministerProductSelectionRequest;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

public class TestDataHelper {
    public RequestHeader createRequestHeader(String channelId) {
        RequestHeader header = new RequestHeaderBuilder().businessTransaction("OfferProductArrangementPCC")
                .channelId(channelId)
                .interactionId("vbww2yofqtcx1qbzw8iz4gm19")
                .serviceRequest("ns4", "AdministerProductSelection", "10.1.1.1", "...")
                .contactPoint("ns4", "003", "0000777505", "Internet Banking", "Browser", "127.0.0.1", "Customer")
                .bapiInformation(channelId, "interactionId", "AAGATEWAY", "ns4")
                .securityHeader("ns4", "UNAUTHSALE")
                .build();
        return header;
    }

    public AdministerProductSelectionRequest createAdministerRequestSameTypeProduct() {
        AdministerProductSelectionRequest request = new AdministerProductSelectionRequest();
        request.setHeader(createRequestHeader("IBL"));
        Product product = createExistingProductAsAdvanceCreditCard();
        request.getExistingProduct().add(product);

        Product appliedProd = createAppliedProductAsAdvanceCreditCard();
        request.setAppliedProduct(appliedProd);

        request.setApplicationTypeCode("SECONDARY");


        return request;
    }

    public AdministerProductSelectionRequest createAdministerRequestDifferentProductAuth() {
        AdministerProductSelectionRequest request = new AdministerProductSelectionRequest();
        request.setHeader(createRequestHeader("IBL"));
        Product product = createExistingProductAsAdvanceCreditCardAuth();
        request.getExistingProduct().add(product);

        Product appliedProd = createAppliedProductAsPlatinumCreditCardForAuth();
        request.setAppliedProduct(appliedProd);

        request.setApplicationTypeCode("SECONDARY");


        return request;
    }

    private Product createAppliedProductAsPlatinumCreditCardForAuth() {
        Product appliedProd = new Product();
        appliedProd.setProductIdentifier("100014");
        appliedProd.setGuaranteedOfferCode("N");
        ExtSysProdIdentifier appExtSysProdIdentifier = new ExtSysProdIdentifier();
        appExtSysProdIdentifier.setSystemCode("00013");
        appExtSysProdIdentifier.setProductIdentifier("190301525302");
        appliedProd.getExternalSystemProductIdentifier().add(appExtSysProdIdentifier);

        appliedProd.setProductName("Platinum Balance Transfer Card");
        appliedProd.setProductType("CC");
        appliedProd.setProductPropositionIdentifier("5");

        InstructionDetails details = new InstructionDetails();
        details.setInstructionMnemonic("P_CC_ADV");
        appliedProd.setInstructionDetails(details);
        return appliedProd;
    }



    private Product createAppliedProductAsAdvanceCreditCard() {
        Product appliedProd = new Product();
        appliedProd.setProductIdentifier("10005");
        appliedProd.setGuaranteedOfferCode("N");
        ExtSysProdIdentifier appExtSysProdIdentifier = new ExtSysProdIdentifier();
        appExtSysProdIdentifier.setSystemCode("00107");
        appExtSysProdIdentifier.setProductIdentifier("120350546780");
        appliedProd.getExternalSystemProductIdentifier().add(appExtSysProdIdentifier);

        ProductOffer appProductOffer = new ProductOffer();
        appProductOffer.setOfferType("2004");
        appProductOffer.setProdOfferIdentifier("1000006");
        appliedProd.getProductoffer().add(appProductOffer);

        appliedProd.setProductName("Advance Credit Card");
        appliedProd.setProductType("3");
        appliedProd.setProductPropositionIdentifier("5");

        InstructionDetails details = new InstructionDetails();
        details.setInstructionMnemonic("P_CC_ADV");
        appliedProd.setInstructionDetails(details);
        return appliedProd;
    }

    private Product createExistingProductAsAdvanceCreditCard() {
        Product product = new Product();
        product.setProductIdentifier("3");
        product.setBrandName("LTB");
        product.setIPRTypeCode(" ");
        product.setRoleCode("001");
        product.setStatusCode("001");

        ExtSysProdIdentifier prodIdentifier = new ExtSysProdIdentifier();
        prodIdentifier.setProductIdentifier("120350546780");
        prodIdentifier.setSystemCode("00013");
        product.getExternalSystemProductIdentifier().add(prodIdentifier);

        product.setProductName("Advance Credit Card");
        product.setProductType("3");
        product.setExtPartyIdTx(" ");
        return product;
    }



    private Product createExistingProductAsAdvanceCreditCardAuth() {
        Product product = new Product();
        ExtSysProdIdentifier prodIdentifier = new ExtSysProdIdentifier();
        prodIdentifier.setProductIdentifier("120350546780");
        prodIdentifier.setSystemCode("00013");
        product.getExternalSystemProductIdentifier().add(prodIdentifier);
        product.setProductType("CC");
        return product;
    }


    public AdministerProductSelectionRequest createAdministerRequestWithDifferentProductTypes() {
        AdministerProductSelectionRequest request = new AdministerProductSelectionRequest();
        request.setHeader(createRequestHeader("IBL"));
        Product product = createExistingProductAsAdvanceCreditCardAuth();
        product.setProductType(null);
        request.getExistingProduct().add(product);

        Product appliedProd = createAppliedProductAsPlatinumCreditCardForAuth();
        request.setAppliedProduct(appliedProd);

        request.setApplicationTypeCode("SECONDARY");
        return  request;
    }
}
