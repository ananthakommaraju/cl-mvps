package com.lloydsbanking.salsa.eligibility.service.rules.agt;

import com.lloydsbanking.salsa.brand.ChannelToBrandMapping;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.ChannelSpecificArrangements;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.soap.fs.user.StAccountListDetail;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.ProductPartyData;
import lb_gbo_sales.messages.RequestHeader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class CheckLoanAndCurrentAccountType {
    private static final int CREDIT_CARD_GROUP_CODE = 3;

    private static final int LOAN_GROUP_CODE = 2;

    @Autowired
    ChannelSpecificArrangements channelSpecificArrangements;

    @Autowired
    ChannelToBrandMapping channelToBrandMapping;

    public boolean hasFEPSLoan(List<ProductPartyData> productPartyDatas) {
        boolean hasFEPSLoan = false;
        for (ProductPartyData productPartyData : productPartyDatas) {

            if (LOAN_GROUP_CODE == productPartyData.getProdGroupId()) {
                hasFEPSLoan = true;

            }

        }
        return hasFEPSLoan;
    }

    public int existingCreditCardGroupCodeProduct(List<ProductPartyData> productPartyDatas) {
        int creditCardCounts = 0;
        for (ProductPartyData productPartyData : productPartyDatas) {

            if (CREDIT_CARD_GROUP_CODE == productPartyData.getProdGroupId()) {
                creditCardCounts++;
            }

        }
        return creditCardCounts;
    }

    public boolean hasCurrentAccountOrLoggedInChannelLoan(RequestHeader header, String currentAccountOrLoanAccountType) throws EligibilityException {

        List<StAccountListDetail> productArrangements;
        try {
            productArrangements = channelSpecificArrangements.getChannelSpecificArrangements(header);
        } catch (SalsaInternalServiceException | SalsaInternalResourceNotAvailableException e) {
            throw new EligibilityException(e);
        }

        String channel = header.getChannelId();
        String brand = channelToBrandMapping.getBrandForChannel(channel).equals("VER") ? "VTB" : channelToBrandMapping.getBrandForChannel(channel);
        boolean hasCurrentAccountOrLoan = false;
        for (StAccountListDetail productArrangement : productArrangements) {
            String accountType = productArrangement.getAccountcategory();
            String channelType = productArrangement.getBrandcode();

            if (accountType.equals(currentAccountOrLoanAccountType) && channelType.trim().equalsIgnoreCase(brand)) {
                hasCurrentAccountOrLoan = true;

            }

        }
        return hasCurrentAccountOrLoan;
    }
}
