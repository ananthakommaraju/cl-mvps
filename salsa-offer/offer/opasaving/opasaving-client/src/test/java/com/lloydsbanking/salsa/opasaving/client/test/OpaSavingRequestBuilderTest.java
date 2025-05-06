package com.lloydsbanking.salsa.opasaving.client.test;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.opasaving.client.*;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@org.junit.experimental.categories.Category(UnitTest.class)
public class OpaSavingRequestBuilderTest {
    @Test
    public void testDepositArrangementBuilder() throws DatatypeConfigurationException {

        OpaSavingRequestBuilder opasavingRequestBuilder = new OpaSavingRequestBuilder();
        DepositArrangementBuilder depositArrangementBuilder = new DepositArrangementBuilder();
        AssociatedProductBuilder associatedProductBuilder = new AssociatedProductBuilder();
        InvolvedPartyBuilder involvedPartyBuilder = new InvolvedPartyBuilder();
        PostalAddressBuilder postalAddressBuilder = new PostalAddressBuilder();
        TelephoneNumberBuilder telephoneNumberBuilder = new TelephoneNumberBuilder();
        IndividualBuilder individualBuilder = new IndividualBuilder();

        RequestHeader header = new OpaSavingRequestHeaderBuilder().businessTransaction("OfferProductArrangementSaving").channelId("LTB").interactionId("vbww2yofqtcx1qbzw8iz4gm19").build();

        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();

        ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
        extSysProdIdentifier.setProductIdentifier("901");
        extSysProdIdentifier.setSystemCode("00107");
        List<ExtSysProdIdentifier> extSysProdIdentifierList = new ArrayList<ExtSysProdIdentifier>();
        extSysProdIdentifierList.add(0, extSysProdIdentifier);

        InstructionDetails instructionDetails = new InstructionDetails();
        instructionDetails.setInstructionMnemonic("P_EASY_SAVR");

        ProductOptions productOptions = new ProductOptions();
        productOptions.setOptionsCode("UP_SELL_DISPLAY_VALUE");
        productOptions.setOptionsValue("0");
        List<ProductOptions> productOptionsList = new ArrayList<ProductOptions>();
        productOptionsList.add(0, productOptions);

        Channel initiatedThrough = new Channel();
        initiatedThrough.setChannelCode("004");
        initiatedThrough.setSubChannelCode("003");

        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setBuildingNumber("23");
        structuredAddress.setHouseName("23");
        structuredAddress.setPostCodeIn("IG11");
        structuredAddress.setPostCodeOut("9JN");
        structuredAddress.setPostTown("London");
        structuredAddress.setPointSuffix("1E");

        PostalAddress postalAddress = postalAddressBuilder.durationOfStay("0404").isBFPOAddressBuilder(false).isPAFFormat(true).statusCode("001").structuredAddress(structuredAddress).build();
        List<PostalAddress> postalAddressList = new ArrayList<PostalAddress>();
        postalAddressList.add(0, postalAddress);

        TelephoneNumber telephoneNumber = telephoneNumberBuilder.countryPhoneCode("44").phoneNumber("7440696125").telephoneType("7").deviceType("Mobile").build();
        List<TelephoneNumber> telephoneNumberList = new ArrayList<TelephoneNumber>();
        telephoneNumberList.add(0, telephoneNumber);

        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(new BigDecimal(300));

        AffiliateDetails affiliateDetails = new AffiliateDetails();
        affiliateDetails.setAffiliateIdentifier("727");
        List<AffiliateDetails> affiliateDetailsList = new ArrayList<AffiliateDetails>();
        affiliateDetailsList.add(0, affiliateDetails);

        RuleCondition condition = new RuleCondition();
        condition.setName("EIDV_REFERRAL_DISABLED_SWITCH");
        condition.setResult("DISABLED");
        List<RuleCondition> conditionList = new ArrayList<RuleCondition>();

        Individual isPlayedBy = individualBuilder.birthDate(datatypeFactory.newXMLGregorianCalendar("1985-03-19T06:40:56.046Z")).nationality("GBR").countryOfBirth("UK").numberOfDependents(new BigInteger("3")).maritalStatus("001").gender("001").employmentStatus("006").currentEmploymentDuration("0707").totalSavingsAmount(currencyAmount).netMonthlyIncome(currencyAmount).monthlyLoanRepaymentAmount(currencyAmount).monthlyMortgageAmount(currencyAmount).otherMonthlyIncomeAmount(currencyAmount).build();

        OfferProductArrangementRequest request = opasavingRequestBuilder.requestHeader(header).depositArrangement(depositArrangementBuilder.arrangementType().associatedProduct(associatedProductBuilder.productIdentifier("20158").externalSystemProductIdentifier(extSysProdIdentifierList).instructionDetails(instructionDetails).productName("Easy Saver").productOptions(productOptionsList).build()).initiatedThrough(initiatedThrough).primaryInvolvedParty(involvedPartyBuilder.partyIdentifier("AAGATEWAY").emailAddress("a@a.com").postalAddress(postalAddressList).telephoneNumber(telephoneNumberList).isPlayedBy(isPlayedBy).userType("1001").internalUserIdentifier("127.0.0.1").partyRole("0001").customerSegment("3").otherBankDuration("0000").build()).marketingPreferenceBySMS(true).applicationType("10001").accountPurpose("BENPA").fundingSource("1").affiliateDetails(affiliateDetailsList).conditions(conditionList).marketingPreferenceByEmail(false).marketingPreferenceByPhone(false).marketingPreferenceByMail(false).build()).build();

        assertSame(header, request.getHeader());
        assertEquals("Arrangement Type not matching", "SA", request.getProductArrangement().getArrangementType());

        assertEquals("ProductIdentifier not matching", "20158", request.getProductArrangement().getAssociatedProduct().getProductIdentifier());
        assertEquals("External system product identifier code not matching", "00107", request.getProductArrangement().getAssociatedProduct().getExternalSystemProductIdentifier().get(0).getSystemCode());
        assertEquals("External system ProductIdentifier not matching", "901", request.getProductArrangement().getAssociatedProduct().getExternalSystemProductIdentifier().get(0).getProductIdentifier());
        assertEquals("Product option code not matching", "UP_SELL_DISPLAY_VALUE", request.getProductArrangement().getAssociatedProduct().getProductoptions().get(0).getOptionsCode());
        assertEquals("Product option value not matching", "0", request.getProductArrangement().getAssociatedProduct().getProductoptions().get(0).getOptionsValue());
        assertEquals("Product name not matching", "Easy Saver", request.getProductArrangement().getAssociatedProduct().getProductName());
        assertEquals("Instruction mnemonic not matching", "P_EASY_SAVR", request.getProductArrangement().getAssociatedProduct().getInstructionDetails().getInstructionMnemonic());

        assertEquals("Channel code not matching", "004", request.getProductArrangement().getInitiatedThrough().getChannelCode());
        assertEquals("Sub Channel code not matching", "003", request.getProductArrangement().getInitiatedThrough().getSubChannelCode());

        assertEquals("Party Identifier not matching", "AAGATEWAY", request.getProductArrangement().getPrimaryInvolvedParty().getPartyIdentifier());
        assertEquals("EmailId not matching", "a@a.com", request.getProductArrangement().getPrimaryInvolvedParty().getEmailAddress());

        assertSame("Structured address not matching", structuredAddress, request.getProductArrangement().getPrimaryInvolvedParty().getPostalAddress().get(0).getStructuredAddress());
        assertSame("Postal Address not matching", postalAddress, request.getProductArrangement().getPrimaryInvolvedParty().getPostalAddress().get(0));
        assertSame("Telephone number nto matching", telephoneNumber, request.getProductArrangement().getPrimaryInvolvedParty().getTelephoneNumber().get(0));
        assertSame("Is Played by nto matching", isPlayedBy, request.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy());

        assertEquals("User type not matching", "1001", request.getProductArrangement().getPrimaryInvolvedParty().getUserType());
        assertEquals("Internal User identifier not matching", "127.0.0.1", request.getProductArrangement().getPrimaryInvolvedParty().getInternalUserIdentifier());
        assertEquals("Party role not matching", "0001", request.getProductArrangement().getPrimaryInvolvedParty().getPartyRole());
        assertEquals("Other Bank duration not matching", "0000", request.getProductArrangement().getPrimaryInvolvedParty().getOtherBankDuration());

        assertTrue("Marketing preference for SMS not matching", request.getProductArrangement().isMarketingPreferenceBySMS());
        assertFalse("Marketing preference for Email not matching", request.getProductArrangement().isMarketingPreferenceByEmail());
        assertFalse("Marketing preference for Mail not matching", request.getProductArrangement().isMarketingPreferenceByMail());
        assertFalse("Marketing preference for Phone not matching", request.getProductArrangement().isMarketingPreferenceByPhone());

    }
}


