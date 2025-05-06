package com.lloydsbanking.salsa.opaloans;

import com.lloydsbanking.salsa.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class LoanAppStatusTest {

    @Test
    public void testLoanAppStatus(){
        assertEquals("LOANAPPSTATUS-BeingProcessed", LoanAppStatus.getLoanAppStatus(LoanAppStatus.BEING_PROCESSED.getKey()));
        assertEquals("LOANAPPSTATUS-Referred", LoanAppStatus.getLoanAppStatus(LoanAppStatus.REFERRED.getKey()));
        assertEquals("LOANAPPSTATUS-ReferInProgress", LoanAppStatus.getLoanAppStatus(LoanAppStatus.REFERIN_PROGRESS.getKey()));
        assertEquals("LOANAPPSTATUS-QuoteGiven", LoanAppStatus.getLoanAppStatus(LoanAppStatus.QUOTE_GIVEN.getKey()));
        assertEquals("LOANAPPSTATUS-IllustrationGiven", LoanAppStatus.getLoanAppStatus(LoanAppStatus.ILLUS_GIVEN.getKey()));
    }
}
