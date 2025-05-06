package com.lloydsbanking.salsa.ppae.service.convert;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.ppae.service.convert.EnquirePaymentRequestFactory;
import com.lloydsbanking.salsa.soap.fs.ftp.EnquirePaymentInstructionFacilityDetailsRequest;
import com.lloydstsb.schema.enterprise.lcsm_arrangement.ProductAccessArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class EnquirePaymentRequestFactoryTest {
    EnquirePaymentRequestFactory enquirePaymentRequestFactory;

    @Before
    public void setUp(){
        enquirePaymentRequestFactory=new EnquirePaymentRequestFactory();
    }

    @Test
    public void testConvert(){
        EnquirePaymentInstructionFacilityDetailsRequest request= enquirePaymentRequestFactory.convert("00012542");
        assertEquals("CHECK_FASTER_PAYMENT_AVAILABILITY",request.getPaymentInstructionIdentifier().getHasEventType().getValue());
        ProductAccessArrangement productAccessArrangement=(ProductAccessArrangement)request.getPaymentInstructionIdentifier().getSourceArrangement().getArrangementAssociations().get(0).getRelatedArrangement();
        assertEquals("00012542",productAccessArrangement.getHasCards().get(0).getObjectReference().getAlternateId().get(0).getValue());
        assertEquals("CREDIT_CARD_NUMBER",productAccessArrangement.getHasCards().get(0).getObjectReference().getAlternateId().get(0).getAttributeString());
    }
}
