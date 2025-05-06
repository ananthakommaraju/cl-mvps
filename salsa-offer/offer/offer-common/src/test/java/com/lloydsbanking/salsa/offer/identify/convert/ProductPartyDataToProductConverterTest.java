package com.lloydsbanking.salsa.offer.identify.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.offer.LegalEntityMapUtility;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.ProductPartyData;
import lib_sim_bo.businessobjects.Product;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Category(UnitTest.class)
public class ProductPartyDataToProductConverterTest {
    private ProductPartyDataToProductConverter productPartyDataToProductConverter;

    private TestDataHelper testDataHelper;

    List<ProductPartyData> productPartyDataList = new ArrayList<>();

    ProductPartyData productPartyData = new ProductPartyData();

    @Before
    public void setUp() {
        productPartyDataList.clear();
        productPartyDataToProductConverter = new ProductPartyDataToProductConverter(new DateFactory());

        testDataHelper = new TestDataHelper();
        Short sysId = 23;
        Long prodGrpId = 777l;
        Map<String, String> legalEntityMap = new HashMap<>();
        legalEntityMap.put("IIL", "LTB");
        LegalEntityMapUtility.setLegalEntityMap(legalEntityMap);

        productPartyData.setIPRTypeCd("ggtt5");
        productPartyData.setProdGroupId(prodGrpId);
        productPartyData.setAmdEffDt("070797");
        productPartyData.setProductHeldOpenDt("10102001");
        productPartyData.setProdHeldRoleCd("wee2112");
        productPartyData.setProdHeldStatusCd("001");
        productPartyData.setExtSysId(sysId);
        productPartyData.setExtProdIdTx("www3");
        productPartyData.setExtProductDs("ttt44");
        productPartyData.setExtPartyIdTx("dddy78");
        productPartyData.setSellerLegalEntCd("IIL");
        productPartyDataList.add(productPartyData);
    }

    @Test
    public void testConvertWithExtProdHeldIdTxNull() throws ParseException, DatatypeConfigurationException {
        productPartyData.setExtProdHeldIdTx(null);
        productPartyData.setProductHeldOpenDt("12042005");
        productPartyData.setAmdEffDt("020298");

        GregorianCalendar gregory = new GregorianCalendar();
        gregory.setTime(FastDateFormat.getInstance("ddMMyyyy").parse("12042005"));
        XMLGregorianCalendar startDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);
        startDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        GregorianCalendar gregory2 = new GregorianCalendar();
        gregory2.setTime(FastDateFormat.getInstance("ddMMyyyy").parse("020298"));
        XMLGregorianCalendar amendmentEffectiveDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory2);
        amendmentEffectiveDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        List<Product> productList = productPartyDataToProductConverter.convert(productPartyDataList, "LTB");
        Product product = productList.get(0);

        assertNull(productPartyData.getExtProdHeldIdTx());
        assertEquals("ggtt5", product.getIPRTypeCode());
        assertEquals(amendmentEffectiveDate, product.getAmendmentEffectiveDate());
        assertEquals("wee2112", product.getRoleCode());
        assertEquals("001", product.getStatusCode());
        assertEquals(startDate, product.getProductoffer().get(0).getStartDate());
        assertEquals("777", product.getProductIdentifier());
        assertEquals("777", product.getProductType());
        assertEquals("ttt44", product.getProductName());
        assertEquals("dddy78", product.getExtPartyIdTx());
        assertEquals("IIL", product.getBrandName());
    }

    @Test
    public void testConvertWithAmdEffDtAsEmpty() throws ParseException, DatatypeConfigurationException {
        productPartyData.setExtProdHeldIdTx(null);
        productPartyData.setProductHeldOpenDt("");
        productPartyData.setAmdEffDt("");
        GregorianCalendar gregory2 = new GregorianCalendar();
        gregory2.setTime(FastDateFormat.getInstance("ddMMyyyy").parse("070797"));

        List<Product> productList = productPartyDataToProductConverter.convert(productPartyDataList, "LTB");
        Product product = productList.get(0);

        assertEquals("ggtt5", product.getIPRTypeCode());
        assertNull(product.getAmendmentEffectiveDate());
        assertEquals("wee2112", product.getRoleCode());
        assertEquals("001", product.getStatusCode());
        assertEquals(null, product.getProductoffer().get(0).getStartDate());
        assertNull(productPartyData.getExtProdHeldIdTx());
        assertEquals("777", product.getProductIdentifier());
        assertEquals("777", product.getProductType());
        assertEquals("ttt44", productPartyData.getExtProductDs());
        assertEquals("dddy78", productPartyData.getExtPartyIdTx());
        assertEquals("IIL", product.getBrandName());
    }

    @Test
    public void testConvertWithExtProdHeldIdTxNotNull() throws ParseException, DatatypeConfigurationException {
        GregorianCalendar gregory = new GregorianCalendar();
        gregory.setTime(FastDateFormat.getInstance("ddMMyyyy").parse("10102001"));
        XMLGregorianCalendar startDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);
        startDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        GregorianCalendar gregory2 = new GregorianCalendar();
        gregory2.setTime(FastDateFormat.getInstance("ddMMyyyy").parse("070797"));
        XMLGregorianCalendar amendmentEffectiveDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory2);
        amendmentEffectiveDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        productPartyData.setExtProdHeldIdTx("6666666666666666666");
        List<Product> productList = productPartyDataToProductConverter.convert(productPartyDataList, "LTB");
        Product product = productList.get(0);

        assertEquals("ggtt5", product.getIPRTypeCode());
        assertEquals(amendmentEffectiveDate, product.getAmendmentEffectiveDate());
        assertEquals("wee2112", product.getRoleCode());
        assertEquals("001", product.getStatusCode());
        assertEquals(startDate, product.getProductoffer().get(0).getStartDate());
        assertEquals("6666666666666666666", product.getExternalProductHeldIdentifier());
        assertEquals("777", product.getProductIdentifier());
        assertEquals("777", product.getProductType());
        assertEquals("ttt44", product.getProductName());
        assertEquals("dddy78", product.getExtPartyIdTx());
        assertEquals("IIL", product.getBrandName());
    }

    @Test
    public void testConvertWithProductHeldOpenDtAsNull() throws ParseException, DatatypeConfigurationException {
        productPartyData.setExtProdHeldIdTx(null);
        productPartyData.setProductHeldOpenDt(null);
        productPartyData.setAmdEffDt("070797");
        GregorianCalendar gregory2 = new GregorianCalendar();
        gregory2.setTime(FastDateFormat.getInstance("ddMMyyyy").parse("070797"));
        XMLGregorianCalendar amendmentEffectiveDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory2);
        amendmentEffectiveDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        List<Product> productList = productPartyDataToProductConverter.convert(productPartyDataList, "LTB");
        Product product = productList.get(0);

        assertEquals("ggtt5", product.getIPRTypeCode());
        assertEquals(amendmentEffectiveDate, product.getAmendmentEffectiveDate());
        assertEquals("wee2112", product.getRoleCode());
        assertEquals("001", product.getStatusCode());
        assertEquals(null, product.getProductoffer().get(0).getStartDate());
        assertNull(productPartyData.getExtProdHeldIdTx());
        assertEquals("777", product.getProductIdentifier());
        assertEquals("777", product.getProductType());
        assertEquals("ttt44", productPartyData.getExtProductDs());
        assertEquals("dddy78", productPartyData.getExtPartyIdTx());
        assertEquals("IIL", product.getBrandName());
    }

    @Test
    public void testConvertWithAmdEffDtAsSpace() throws ParseException, DatatypeConfigurationException {
        productPartyData.setAmdEffDt(null);
        List<Product> productList = productPartyDataToProductConverter.convert(productPartyDataList, "LTB");
        assertNull(productList.get(0).getAmendmentEffectiveDate());

        productPartyData.setAmdEffDt("");
        List<Product> productList1 = productPartyDataToProductConverter.convert(productPartyDataList, "LTB");
        assertNull(productList.get(0).getAmendmentEffectiveDate());

        productPartyData.setAmdEffDt("070797");
        List<Product> productList2 = productPartyDataToProductConverter.convert(productPartyDataList, "LTB");
        assertNull(productList.get(0).getAmendmentEffectiveDate());

        productPartyData.setAmdEffDt("  ");
        List<Product> productList3 = productPartyDataToProductConverter.convert(productPartyDataList, "LTB");
        assertNull(productList.get(0).getAmendmentEffectiveDate());

        productPartyData.setAmdEffDt("2016/08/1");
        List<Product> productList4 = productPartyDataToProductConverter.convert(productPartyDataList, "LTB");
        assertNull(productList.get(0).getAmendmentEffectiveDate());

        productPartyData.setAmdEffDt("A");
        List<Product> productList5 = productPartyDataToProductConverter.convert(productPartyDataList, "LTB");
        assertNull(productList.get(0).getAmendmentEffectiveDate());

        productPartyData.setAmdEffDt("0");
        List<Product> productList6 = productPartyDataToProductConverter.convert(productPartyDataList, "LTB");
        assertNull(productList.get(0).getAmendmentEffectiveDate());

        productPartyData.setAmdEffDt("\0");
        List<Product> productList7 = productPartyDataToProductConverter.convert(productPartyDataList, "LTB");
        assertNull(productList.get(0).getAmendmentEffectiveDate());









    }
}
