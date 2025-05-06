package com.lloydsbanking.salsa.apapca.service.fulfil.converter;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.xml.schema.pega.industryaccountswitching.NewAccountType;
import com.lloydsbanking.xml.schema.pega.industryaccountswitching.OldAccountType;
import com.lloydsbanking.xml.schema.pega.industryaccountswitching.PartyPostalAddressType;
import lib_sim_bo.businessobjects.*;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class CreateCaseAccountFactoryTest {
    private CreateCaseAccountFactory createCaseAccountFactory;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        createCaseAccountFactory = new CreateCaseAccountFactory();
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void testCreateOldAccountType() {
        DirectDebit accountSwitchingDetails = new DirectDebit();
        accountSwitchingDetails.setAccountNumber("000123548");
        accountSwitchingDetails.setSortCode("01254");
        accountSwitchingDetails.setAccountHolderName("holder");
        accountSwitchingDetails.setBankName("lloyds");
        OldAccountType oldAccount = createCaseAccountFactory.createOldAccountType(accountSwitchingDetails);
        assertEquals("000123548", oldAccount.getAccountNumber());
        assertEquals("01254", oldAccount.getSortCode());
        assertEquals("holder", oldAccount.getAccountName());
        assertEquals("lloyds", oldAccount.getBankName());
    }

    @Test
    public void testCreateOldAccountTypeWithNullAccountSwitchingDetails() {
        OldAccountType oldAccount = createCaseAccountFactory.createOldAccountType(null);
        assertNotNull(oldAccount);
        assertNull(oldAccount.getAccountNumber());
        assertNull(oldAccount.getSortCode());
        assertNull(oldAccount.getAccountName());
        assertNull( oldAccount.getBankName());
    }

    @Test
    public void testCreateOldAccountTypeWithNewDirectDebit() {
        DirectDebit accountSwitchingDetails = new DirectDebit();
        OldAccountType oldAccount = createCaseAccountFactory.createOldAccountType(accountSwitchingDetails);
        assertNull(oldAccount.getAccountNumber());
    }

    @Test
    public void testCreateNewAccount() {
        NewAccountType newAccount = createCaseAccountFactory.createNewAccount(createDepositArrangement(), new PartyPostalAddressType(), "IBL");
        assertEquals("10254", newAccount.getSortCode());
        assertEquals("00002145236", newAccount.getAccountNumber());
        assertEquals(BigDecimal.valueOf(51212121), newAccount.getBalanceTransferFundingLimit().getValue());
        assertEquals("PRIM", newAccount.getAccountParty().get(0).getAccountPartyType());
        assertEquals("George", newAccount.getAccountParty().get(0).getPartyName().getFirstName());
    }
    
    @Test
    public void testCreateNewAccountWithDefault(){
        DepositArrangement depositArrangement = new DepositArrangement();
        depositArrangement.setAccountSwitchingDetails(new DirectDebit());
        Individual individual = new Individual();
        IndividualName individualName = new IndividualName();
        individual.getIndividualName().add(individualName);
        depositArrangement.setPrimaryInvolvedParty(new Customer());
        depositArrangement.getPrimaryInvolvedParty().setIsPlayedBy(individual);
        NewAccountType newAccount = createCaseAccountFactory.createNewAccount(depositArrangement, new PartyPostalAddressType(), "IBH");
        assertEquals("PRIM", newAccount.getAccountParty().get(0).getAccountPartyType());
        assertTrue(newAccount.getAccountParty().get(0).isAuthorityIndicator());
    }

    @Test
    public void testCreateNewAccountWithNullAccountSwitchingDetails(){
        DepositArrangement depositArrangement = new DepositArrangement();
        Individual individual = new Individual();
        IndividualName individualName = new IndividualName();
        individual.getIndividualName().add(individualName);
        depositArrangement.setPrimaryInvolvedParty(new Customer());
        depositArrangement.getPrimaryInvolvedParty().setIsPlayedBy(individual);
        NewAccountType newAccount = createCaseAccountFactory.createNewAccount(depositArrangement, new PartyPostalAddressType(), "IBS");
        assertEquals("PRIM", newAccount.getAccountParty().get(0).getAccountPartyType());
        assertTrue(newAccount.getAccountParty().get(0).isAuthorityIndicator());
    }

    @Test
    public void testCreateNewAccountWithNullSortCode(){
        DepositArrangement depositArrangement=createDepositArrangement();
        depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode(null);
        depositArrangement.getAccountSwitchingDetails().setAmount(null);
        depositArrangement.getFinancialInstitution().getHasOrganisationUnits().clear();
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().clear();
        NewAccountType newAccount = createCaseAccountFactory.createNewAccount(depositArrangement, new PartyPostalAddressType(), "IBV");
        assertEquals("PRIM", newAccount.getAccountParty().get(0).getAccountPartyType());
        assertTrue(newAccount.getAccountParty().get(0).isAuthorityIndicator());
    }

    @Test
    public void testCreateNewAccountWithUnKnownChannelId(){
        DepositArrangement depositArrangement=createDepositArrangement();
        depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode(null);
        depositArrangement.getAccountSwitchingDetails().setAmount(null);
        depositArrangement.getFinancialInstitution().getHasOrganisationUnits().clear();
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().clear();
        NewAccountType newAccount = createCaseAccountFactory.createNewAccount(depositArrangement, new PartyPostalAddressType(), "IBR");
        assertEquals("PRIM", newAccount.getAccountParty().get(0).getAccountPartyType());
        assertTrue(newAccount.getAccountParty().get(0).isAuthorityIndicator());
    }

    @Test
    public void testCreateNewAccountWithNullAmount(){
        DepositArrangement depositArrangement=createDepositArrangement();
        depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode(null);
        depositArrangement.getAccountSwitchingDetails().setAmount(new CurrencyAmount());
        depositArrangement.getAccountSwitchingDetails().getAmount().setAmount(null);
        depositArrangement.getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode("");
        depositArrangement.getPrimaryInvolvedParty().setIsPlayedBy(null);
        NewAccountType newAccount = createCaseAccountFactory.createNewAccount(depositArrangement, new PartyPostalAddressType(), null);
        assertEquals("PRIM", newAccount.getAccountParty().get(0).getAccountPartyType());
        assertTrue(newAccount.getAccountParty().get(0).isAuthorityIndicator());
    }



    private DepositArrangement createDepositArrangement() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangementResp();
        depositArrangement.setAccountSwitchingDetails(new DirectDebit());
        depositArrangement.getAccountSwitchingDetails().setTextAlert("ALERT");
        depositArrangement.getAccountSwitchingDetails().setMobileNumber("9876543210");
        depositArrangement.getAccountSwitchingDetails().setCardNumber("124587456213654789");
        depositArrangement.getAccountSwitchingDetails().setCardExpiryDate("22/32");
        CurrencyAmount currencyAmount = new CurrencyAmount();
        currencyAmount.setAmount(BigDecimal.valueOf(51212121));
        depositArrangement.getAccountSwitchingDetails().setAmount(currencyAmount);
        depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode("10254");
        depositArrangement.setAccountNumber("00002145236");
        depositArrangement.setPrimaryInvolvedParty(new Customer());
        depositArrangement.getPrimaryInvolvedParty().setCustomerIdentifier("140254");
        Individual individual = new Individual();
        IndividualName individualName = new IndividualName();
        individualName.setPrefixTitle("Mr.");
        individualName.setFirstName("George");
        individualName.getMiddleNames().add("BD");
        individualName.setLastName("Cloney");
        individual.getIndividualName().add(individualName);
        depositArrangement.getPrimaryInvolvedParty().setIsPlayedBy(individual);
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setNationality("Indian");
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().setBirthDate(new DateFactory().stringToXMLGregorianCalendar("22101993", FastDateFormat.getInstance("ddmmyyyy")));
        return depositArrangement;
    }
}
