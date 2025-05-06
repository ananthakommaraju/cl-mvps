package com.lloydsbanking.salsa.offer.apply.evaluate;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.offer.apply.convert.AsmResponseToProductOptionsConverter;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.lloydsbanking.salsa.soap.asm.f205.objects.F205Resp;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_bo.businessobjects.ProductOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ProductOptionsEvaluatorTest {

    private ProductOptionsEvaluator productOptionsEvaluator;

    @Before
    public void setUp() {
        productOptionsEvaluator = new ProductOptionsEvaluator();
        productOptionsEvaluator.offerLookupDataRetriever = mock(LookupDataRetriever.class);
        productOptionsEvaluator.asmResponseToProductOptionsConverter = mock(AsmResponseToProductOptionsConverter.class);
    }

    @Test
    public void testGetOptionCodeForCreditCard() throws DataNotAvailableErrorMsg {
        String channelId = "1";
        F205Resp f205Resp = new F205Resp();

        List<ReferenceDataLookUp> ccFamilyCodes = new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp = new ReferenceDataLookUp();
        referenceDataLookUp.setDescription("desc");
        referenceDataLookUp.setGroupCode("CC_CROSS_SELL_FC");
        referenceDataLookUp.setLookupValueDesc("170");
        ccFamilyCodes.add(referenceDataLookUp);

        ReferenceDataLookUp referenceDataLookUp1 = new ReferenceDataLookUp();
        referenceDataLookUp1.setDescription("description");
        referenceDataLookUp1.setGroupCode("CC_CROSS_SELL");
        referenceDataLookUp1.setLookupValueDesc("100");
        ccFamilyCodes.add(referenceDataLookUp1);

        ReferenceDataLookUp referenceDataLookUp2 = new ReferenceDataLookUp();
        referenceDataLookUp2.setDescription("description");
        referenceDataLookUp2.setGroupCode("CC_CROSS_SELL_FC");
        referenceDataLookUp2.setLookupValueDesc("150");
        ccFamilyCodes.add(referenceDataLookUp2);

        List<ProductOptions> productOptionsList = new ArrayList<>();
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsDescription("opDesc");
        productOptions.setOptionsCode("101");
        productOptions.setOptionsValue("12");
        productOptionsList.add(productOptions);

        ProductOptions productOptions1 = new ProductOptions();
        productOptions1.setOptionsDescription("opDesc2");
        productOptions1.setOptionsCode("2");
        productOptions1.setOptionsValue("13");
        productOptionsList.add(productOptions1);

        ProductOptions productOptions2 = new ProductOptions();
        productOptions2.setOptionsDescription("opDesc3");
        productOptions2.setOptionsCode("3");
        productOptions2.setOptionsValue("14");
        productOptionsList.add(productOptions2);


        when(productOptionsEvaluator.offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(any(String.class), any(ArrayList.class))).thenReturn(ccFamilyCodes);
        when(productOptionsEvaluator.asmResponseToProductOptionsConverter.creditScoreResponseToProductOptionsConverter(any(F205Resp.class))).thenReturn(productOptionsList);

        productOptionsList = productOptionsEvaluator.getProductOptions(f205Resp, channelId);

        assertEquals(2, productOptionsList.size());
        assertEquals("Y", productOptionsList.get(0).getOptionsValue());
        assertEquals("CHECK_BOOK_OFFERED_FLAG", productOptionsList.get(0).getOptionsCode());
        assertEquals("2", productOptionsList.get(1).getOptionsValue());
        assertEquals("DEBIT_CARD_RISK_CODE", productOptionsList.get(1).getOptionsCode());


    }

    @Test
    public void testGetOptionCodeForCaseOne() throws DataNotAvailableErrorMsg {
        String channelId = "1";
        F205Resp f205Resp = new F205Resp();

        List<ReferenceDataLookUp> ccFamilyCodes = new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp = new ReferenceDataLookUp();
        referenceDataLookUp.setDescription("desc");
        referenceDataLookUp.setGroupCode("CC_CROSS_SELL_FC");
        referenceDataLookUp.setLookupValueDesc("170");
        ccFamilyCodes.add(referenceDataLookUp);

        ReferenceDataLookUp referenceDataLookUp1 = new ReferenceDataLookUp();
        referenceDataLookUp1.setDescription("description");
        referenceDataLookUp1.setGroupCode("CC_CROSS_SELL");
        referenceDataLookUp1.setLookupValueDesc("100");
        ccFamilyCodes.add(referenceDataLookUp1);

        ReferenceDataLookUp referenceDataLookUp2 = new ReferenceDataLookUp();
        referenceDataLookUp2.setDescription("description");
        referenceDataLookUp2.setGroupCode("CC_CROSS_SELL_FC");
        referenceDataLookUp2.setLookupValueDesc("150");
        ccFamilyCodes.add(referenceDataLookUp2);

        List<ProductOptions> productOptionsList = new ArrayList<>();
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsDescription("opDesc");
        productOptions.setOptionsCode("102");
        productOptions.setOptionsValue("12");
        productOptionsList.add(productOptions);

        ProductOptions productOptions1 = new ProductOptions();
        productOptions1.setOptionsDescription("opDesc2");
        productOptions1.setOptionsCode("110");
        productOptions1.setOptionsValue("13");
        productOptionsList.add(productOptions1);

        ProductOptions productOptions2 = new ProductOptions();
        productOptions2.setOptionsDescription("opDesc3");
        productOptions2.setOptionsCode("3");
        productOptions2.setOptionsValue("14");
        productOptionsList.add(productOptions2);


        when(productOptionsEvaluator.offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(any(String.class), any(ArrayList.class))).thenReturn(ccFamilyCodes);
        when(productOptionsEvaluator.asmResponseToProductOptionsConverter.creditScoreResponseToProductOptionsConverter(any(F205Resp.class))).thenReturn(productOptionsList);

        productOptionsList = productOptionsEvaluator.getProductOptions(f205Resp, channelId);

        assertEquals(2, productOptionsList.size());
        assertEquals("12", productOptionsList.get(0).getOptionsValue());
        assertEquals("OVERDRAFT_OFFERED_FLAG", productOptionsList.get(0).getOptionsCode());
        assertEquals("3", productOptionsList.get(1).getOptionsValue());
        assertEquals("DEBIT_CARD_RISK_CODE", productOptionsList.get(1).getOptionsCode());


    }

    @Test
    public void testGetOptionCodeForCaseTwo() throws DataNotAvailableErrorMsg {
        String channelId = "1";
        F205Resp f205Resp = new F205Resp();

        List<ReferenceDataLookUp> ccFamilyCodes = new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp = new ReferenceDataLookUp();
        referenceDataLookUp.setDescription("desc");
        referenceDataLookUp.setGroupCode("CC_CROSS_SELL_FC");
        referenceDataLookUp.setLookupValueDesc("170");
        ccFamilyCodes.add(referenceDataLookUp);

        ReferenceDataLookUp referenceDataLookUp1 = new ReferenceDataLookUp();
        referenceDataLookUp1.setDescription("description");
        referenceDataLookUp1.setGroupCode("CC_CROSS_SELL");
        referenceDataLookUp1.setLookupValueDesc("100");
        ccFamilyCodes.add(referenceDataLookUp1);

        ReferenceDataLookUp referenceDataLookUp2 = new ReferenceDataLookUp();
        referenceDataLookUp2.setDescription("description");
        referenceDataLookUp2.setGroupCode("CC_CROSS_SELL_FC");
        referenceDataLookUp2.setLookupValueDesc("150");
        ccFamilyCodes.add(referenceDataLookUp2);

        List<ProductOptions> productOptionsList = new ArrayList<>();
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsDescription("opDesc");
        productOptions.setOptionsCode("170");
        productOptions.setOptionsValue("12");
        productOptionsList.add(productOptions);

        ProductOptions productOptions1 = new ProductOptions();
        productOptions1.setOptionsDescription("opDesc2");
        productOptions1.setOptionsCode("110");
        productOptions1.setOptionsValue("13");
        productOptionsList.add(productOptions1);

        ProductOptions productOptions2 = new ProductOptions();
        productOptions2.setOptionsDescription("opDesc3");
        productOptions2.setOptionsCode("3");
        productOptions2.setOptionsValue("14");
        productOptionsList.add(productOptions2);


        when(productOptionsEvaluator.offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(any(String.class), any(ArrayList.class))).thenReturn(ccFamilyCodes);
        when(productOptionsEvaluator.asmResponseToProductOptionsConverter.creditScoreResponseToProductOptionsConverter(any(F205Resp.class))).thenReturn(productOptionsList);

        productOptionsList = productOptionsEvaluator.getProductOptions(f205Resp, channelId);

        assertEquals(3, productOptionsList.size());
        assertEquals("12", productOptionsList.get(0).getOptionsValue());
        assertEquals("CREDIT_CARD_OFFERED_FLAG", productOptionsList.get(0).getOptionsCode());
        assertEquals("170", productOptionsList.get(1).getOptionsValue());
        assertEquals("CREDIT_CARD_FAMILY_CODE", productOptionsList.get(1).getOptionsCode());
        assertEquals("3", productOptionsList.get(2).getOptionsValue());
        assertEquals("DEBIT_CARD_RISK_CODE", productOptionsList.get(2).getOptionsCode());


    }

    @Test
    public void testGetOptionCodeForDebitCardRiskCode() throws DataNotAvailableErrorMsg {
        String channelId = "1";
        F205Resp f205Resp = new F205Resp();

        List<ReferenceDataLookUp> ccFamilyCodes = new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp = new ReferenceDataLookUp();
        referenceDataLookUp.setDescription("desc");
        referenceDataLookUp.setGroupCode("abc");
        referenceDataLookUp.setLookupValueDesc("lookUpValueDesc");
        ccFamilyCodes.add(referenceDataLookUp);

        List<ProductOptions> productOptionsList = new ArrayList<>();
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsDescription("opDesc");
        productOptions.setOptionsCode("200");
        productOptionsList.add(productOptions);

        when(productOptionsEvaluator.offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(any(String.class), any(ArrayList.class))).thenReturn(ccFamilyCodes);
        when(productOptionsEvaluator.asmResponseToProductOptionsConverter.creditScoreResponseToProductOptionsConverter(any(F205Resp.class))).thenReturn(productOptionsList);


        productOptionsList = productOptionsEvaluator.getProductOptions(f205Resp, channelId);

        assertEquals("0000", productOptionsList.get(0).getOptionsValue());
        assertEquals("DEBIT_CARD_RISK_CODE", productOptionsList.get(0).getOptionsCode());

    }

    @Test
    public void testGetOptionCodeForOverdraftOfferedFlag() throws DataNotAvailableErrorMsg {
        String channelId = "1";
        F205Resp f205Resp = new F205Resp();

        List<ReferenceDataLookUp> ccFamilyCodes = new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp = new ReferenceDataLookUp();
        referenceDataLookUp.setDescription("desc");
        referenceDataLookUp.setGroupCode("abc");
        referenceDataLookUp.setLookupValueDesc("lookUpValueDesc");
        ccFamilyCodes.add(referenceDataLookUp);

        List<ProductOptions> productOptionsList = new ArrayList<>();
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsDescription("opDesc");
        productOptions.setOptionsCode("102");
        productOptions.setOptionsValue("value");
        productOptionsList.add(productOptions);

        when(productOptionsEvaluator.offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(any(String.class), any(ArrayList.class))).thenReturn(ccFamilyCodes);
        when(productOptionsEvaluator.asmResponseToProductOptionsConverter.creditScoreResponseToProductOptionsConverter(any(F205Resp.class))).thenReturn(productOptionsList);


        productOptionsList = productOptionsEvaluator.getProductOptions(f205Resp, channelId);

        assertEquals("value", productOptionsList.get(0).getOptionsValue());
        assertEquals("OVERDRAFT_OFFERED_FLAG", productOptionsList.get(0).getOptionsCode());

    }

    @Test
    public void testGetOptionCodeForCheckBookOfferedFlag() throws DataNotAvailableErrorMsg {
        String channelId = "1";
        F205Resp f205Resp = new F205Resp();

        List<ReferenceDataLookUp> ccFamilyCodes = new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp = new ReferenceDataLookUp();
        referenceDataLookUp.setDescription("desc");
        referenceDataLookUp.setGroupCode("abc");
        referenceDataLookUp.setLookupValueDesc("lookUpValueDesc");
        ccFamilyCodes.add(referenceDataLookUp);

        List<ProductOptions> productOptionsList = new ArrayList<>();
        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsDescription("opDesc");
        productOptions.setOptionsCode("101");
        productOptions.setOptionsValue("value");
        productOptionsList.add(productOptions);

        when(productOptionsEvaluator.offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(any(String.class), any(ArrayList.class))).thenReturn(ccFamilyCodes);
        when(productOptionsEvaluator.asmResponseToProductOptionsConverter.creditScoreResponseToProductOptionsConverter(any(F205Resp.class))).thenReturn(productOptionsList);


        productOptionsList = productOptionsEvaluator.getProductOptions(f205Resp, channelId);

        assertEquals("Y", productOptionsList.get(0).getOptionsValue());
        assertEquals("CHECK_BOOK_OFFERED_FLAG", productOptionsList.get(0).getOptionsCode());

    }
}
