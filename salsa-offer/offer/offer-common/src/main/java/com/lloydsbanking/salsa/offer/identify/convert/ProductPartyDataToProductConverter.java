package com.lloydsbanking.salsa.offer.identify.convert;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.offer.LegalEntityMapUtility;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.ProductPartyData;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductOffer;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

public class ProductPartyDataToProductConverter {
    private DateFactory dateFactory;

    private static final Logger LOGGER = Logger.getLogger(ProductPartyDataToProductConverter.class);

    private static final int EXT_PROD_HELD_ID_TX_LENGTH = 19;

    @Autowired
    public ProductPartyDataToProductConverter(DateFactory dateFactory) {
        this.dateFactory = dateFactory;
    }

    public List<Product> convert(List<ProductPartyData> productPartyDataList, String brand) {
        List<Product> productList = new ArrayList<Product>();
        for (ProductPartyData productPartyData : productPartyDataList) {
            if (isLoggedInBrandMatchesProductBrand(productPartyData, brand)) {
                Product product = new Product();

                product.setProductIdentifier(String.valueOf(productPartyData.getProdGroupId()));
                product.setBrandName(productPartyData.getSellerLegalEntCd());
                product.setIPRTypeCode(productPartyData.getIPRTypeCd());
                product.setRoleCode(productPartyData.getProdHeldRoleCd());
                product.setStatusCode(productPartyData.getProdHeldStatusCd());
                product.setAmendmentEffectiveDate(stringToXMLGregorianConversion(productPartyData.getAmdEffDt()));

                ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
                extSysProdIdentifier.setSystemCode(createSystemCode(productPartyData.getExtSysId()));
                extSysProdIdentifier.setProductIdentifier(productPartyData.getExtProdIdTx());
                product.getExternalSystemProductIdentifier().add(extSysProdIdentifier);

                ProductOffer productOffer = new ProductOffer();
                productOffer.setStartDate(stringToXMLGregorianConversion(productPartyData.getProductHeldOpenDt()));
                product.getProductoffer().add(productOffer);

                product.setProductName(productPartyData.getExtProductDs());
                product.setProductType(String.valueOf(productPartyData.getProdGroupId()));
                product.setExtPartyIdTx(productPartyData.getExtPartyIdTx());

                if (productPartyData.getExtProdHeldIdTx() != null && productPartyData.getExtProdHeldIdTx().length() == EXT_PROD_HELD_ID_TX_LENGTH) {
                    product.setExternalProductHeldIdentifier(productPartyData.getExtProdHeldIdTx());
                }
                productList.add(product);
            }
        }
        return productList;
    }

    private boolean isLoggedInBrandMatchesProductBrand(ProductPartyData productPartyData, String brand) {
        String brandInOcis = LegalEntityMapUtility.getLegalEntityMap().get(productPartyData.getSellerLegalEntCd());
        LOGGER.info("Filter Products If Brand Not Matching, BrandInOcis | ProductActualBrand ; " + brandInOcis + " | " + brand);
        if (brand.equalsIgnoreCase(brandInOcis)) {
            return true;
        }
        return false;
    }

    private XMLGregorianCalendar stringToXMLGregorianConversion(String stringData) {
        if (null != stringData) {
            XMLGregorianCalendar birthDate = dateFactory.stringToXMLGregorianCalendar(stringData, FastDateFormat.getInstance("ddMMyyyy"));
            if (null != birthDate) {
                birthDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            }
            return birthDate;
        }
        return null;

    }

    private String createSystemCode(int code) {
        return String.format("%05d", code);
    }
}