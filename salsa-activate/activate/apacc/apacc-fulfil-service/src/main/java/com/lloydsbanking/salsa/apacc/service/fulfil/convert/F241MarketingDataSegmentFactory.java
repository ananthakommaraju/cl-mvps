package com.lloydsbanking.salsa.apacc.service.fulfil.convert;


import com.lloydsbanking.salsa.soap.fdi.f241.objects.CoOwnerMarketingChannels;
import com.lloydsbanking.salsa.soap.fdi.f241.objects.MarketingDataSegment;
import com.lloydsbanking.salsa.soap.fdi.f241.objects.OwnerMarketingChannels;
import com.lloydsbanking.salsa.soap.fdi.f241.objects.OwnerMarketingProducts;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import org.springframework.stereotype.Component;

@Component
public class F241MarketingDataSegmentFactory {

    public MarketingDataSegment getMarketingDataSegment(FinanceServiceArrangement financeServiceArrangement) {
        MarketingDataSegment marketingDataSegment = new MarketingDataSegment();
        marketingDataSegment.setCoOwnerMarketingChannels(getCoOwnerMarketingChannels());
        marketingDataSegment.setOwnerMarketingChannels(getOwnerMarketingChannels(financeServiceArrangement));
        marketingDataSegment.setOwnerMarketingProducts(getOwnerMarketingProduct(financeServiceArrangement.isIsJointParty()));
        return marketingDataSegment;
    }

    private CoOwnerMarketingChannels getCoOwnerMarketingChannels() {
        CoOwnerMarketingChannels coOwnerMarketingChannels = new CoOwnerMarketingChannels();
        coOwnerMarketingChannels.setStmtMktgChanIn("0");
        coOwnerMarketingChannels.setDirectMarketingIn("0");
        coOwnerMarketingChannels.setCrdMktgChanIn("0");
        coOwnerMarketingChannels.setPINMktgChanIn("0");
        coOwnerMarketingChannels.setMailMktIn("0");
        coOwnerMarketingChannels.setInternetMktgChanIn("0");
        coOwnerMarketingChannels.setPhoneMktIn("0");
        coOwnerMarketingChannels.setIDTVMktgChanIn("0");
        coOwnerMarketingChannels.setBranchMktgChanIn("0");
        coOwnerMarketingChannels.setOutboundMktgChanIn("0");
        coOwnerMarketingChannels.setEmailAddressIn("0");
        coOwnerMarketingChannels.setSMSComfirmationIn("0");
        coOwnerMarketingChannels.setAcctUser1Cd("0");
        coOwnerMarketingChannels.setAcctUser2Cd("0");
        coOwnerMarketingChannels.setAcctUser3Cd("0");
        coOwnerMarketingChannels.setAcctUser4Cd("0");
        coOwnerMarketingChannels.setAcctUser5Cd("0");
        coOwnerMarketingChannels.setAcctUser6Cd("0");
        coOwnerMarketingChannels.setAcctUser7Cd("0");
        coOwnerMarketingChannels.setAcctUser8Cd("0");
        return coOwnerMarketingChannels;
    }

    private OwnerMarketingChannels getOwnerMarketingChannels(FinanceServiceArrangement financeServiceArrangement) {
        OwnerMarketingChannels ownerMarketingChannels = new OwnerMarketingChannels();
        Customer customer = financeServiceArrangement.getPrimaryInvolvedParty();
        if (!isBooleanNotNullAndTrue(financeServiceArrangement.isIsJointParty())) {
            if (customer.getCustomerNumber() != null) {

                if (isBooleanNotNullAndTrue(financeServiceArrangement.isMarketingPreferenceByMail())) {
                    ownerMarketingChannels.setMailMktIn("1");
                } else {
                    ownerMarketingChannels.setMailMktIn("0");
                }
                if (isBooleanNotNullAndTrue(financeServiceArrangement.isMarketingPreferenceByPhone())) {
                    ownerMarketingChannels.setPhoneMktIn("1");
                } else {
                    ownerMarketingChannels.setPhoneMktIn("0");
                }
                if (isBooleanNotNullAndTrue(financeServiceArrangement.isMarketingPreferenceByEmail())) {
                    ownerMarketingChannels.setEmailAddressIn("1");
                } else {
                    ownerMarketingChannels.setEmailAddressIn("0");
                }
                if (isBooleanNotNullAndTrue(financeServiceArrangement.isMarketingPreferenceBySMS())) {
                    ownerMarketingChannels.setSMSComfirmationIn("1");
                } else {
                    ownerMarketingChannels.setSMSComfirmationIn("0");
                }
            }
        }
        ownerMarketingChannels.setStmtMktgChanIn("0");
        ownerMarketingChannels.setDirectMarketingIn("0");
        ownerMarketingChannels.setCrdMktgChanIn("0");
        ownerMarketingChannels.setPINMktgChanIn("0");
        ownerMarketingChannels.setInternetMktgChanIn("0");
        ownerMarketingChannels.setIDTVMktgChanIn("0");
        ownerMarketingChannels.setBranchMktgChanIn("0");
        ownerMarketingChannels.setOutboundMktgChanIn("0");
        ownerMarketingChannels.setAcctUserCd("0");
        ownerMarketingChannels.setAcctUser1Cd("0");
        ownerMarketingChannels.setAcctUser2Cd("0");
        ownerMarketingChannels.setAcctUser3Cd("0");
        ownerMarketingChannels.setAcctUser4Cd("0");
        ownerMarketingChannels.setAcctUser5Cd("0");
        ownerMarketingChannels.setAcctUser6Cd("0");
        ownerMarketingChannels.setAcctUser7Cd("0");
        return ownerMarketingChannels;
    }

    private OwnerMarketingProducts getOwnerMarketingProduct(Boolean isJointParty) {
        OwnerMarketingProducts ownerMarketingProducts = null;
        if (!isBooleanNotNullAndTrue(isJointParty)) {
            ownerMarketingProducts = new OwnerMarketingProducts();
            ownerMarketingProducts.setPPIInformationIn("1");
            ownerMarketingProducts.setCardProctectionIn("1");
            ownerMarketingProducts.setSavingsInformationIn("1");
            ownerMarketingProducts.setUnsecuredLoansIn("1");
            ownerMarketingProducts.setMortgageInformationIn("1");
            ownerMarketingProducts.setCoreBnkgInfoIn("1");
            ownerMarketingProducts.setCarInsInfoIn("1");
            ownerMarketingProducts.setHomeInsInfoIn("1");
            ownerMarketingProducts.setLifeInsInfoIn("1");
            ownerMarketingProducts.setAcctUserCd("1");
            ownerMarketingProducts.setAcctUser1Cd("1");
            ownerMarketingProducts.setAcctUser2Cd("1");
            ownerMarketingProducts.setAcctUser3Cd("1");
            ownerMarketingProducts.setAcctUser4Cd("1");
            ownerMarketingProducts.setAcctUser5Cd("1");
            ownerMarketingProducts.setAcctUser6Cd("1");
            ownerMarketingProducts.setAcctUser7Cd("1");
            ownerMarketingProducts.setAcctUser8Cd("1");
            ownerMarketingProducts.setAcctUser9Cd("1");
            ownerMarketingProducts.setAcctUser10Cd("1");
        }
        return ownerMarketingProducts;
    }

    private boolean isBooleanNotNullAndTrue(Boolean aBoolean) {
        return (aBoolean != null && aBoolean);
    }
}
