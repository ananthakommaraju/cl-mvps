package com.lloydsbanking.salsa.eligibility.service.rules.cst;

import com.lloydsbanking.salsa.downstream.ref.jdbc.RefLookupDao;
import com.lloydsbanking.salsa.downstream.ref.model.RefLookupDto;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.AccountEventRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalBusinessException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CR057LoanAppliedTimesRule implements CSTEligibilityRule {
    private static final Logger LOGGER = Logger.getLogger(CR057LoanAppliedTimesRule.class);

    @Autowired
    RefLookupDao refLookupDao;

    @Autowired
    AccountEventRetriever accountEventRetriever;

    List<String> groupCodeList = Arrays.asList("RBB_LOAN_ACCPT", "RBB_LOAN_DCLN", "RBB_OD_ACCPT", "RBB_OD_DCLN", "TRKNG_EVENT_IDS");


    List<String> forbiddenGroupCodeList = Arrays.asList("RBB_LOAN_ACCPT", "RBB_LOAN_DCLN", "RBB_OD_ACCPT", "RBB_OD_DCLN");

    String trackingEventId = "TRKNG_EVENT_IDS";

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, XMLGregorianCalendar birthDate, String sortCode, String customerId) throws EligibilityException {

        List<RefLookupDto> refLookupDtos = refLookupDao.findByChannelAndGroupCdIn(ruleDataHolder.getHeader().getChannelId(), groupCodeList);

        List<String> forbiddenEventList = new ArrayList<>();

        String eventId = null;

        for (RefLookupDto refLookupDto : refLookupDtos) {
            if (forbiddenGroupCodeList.contains(refLookupDto.getGroupCd())) {
                forbiddenEventList.add(refLookupDto.getLookupValSd());
            }

            if (trackingEventId.equalsIgnoreCase(refLookupDto.getGroupCd())) {
                eventId = refLookupDto.getLookupTxt();
            }
        }

        List<String> accountEventList = getEventList(ruleDataHolder, eventId);

        int count = getMatchedEventCount(forbiddenEventList, accountEventList);

        String[] parameterValues = ruleDataHolder.getRuleParamValue().split(":");
        if (count >= Integer.parseInt(parameterValues[1])) {
            return new EligibilityDecision("Customer has applied for loan/overdraft " + count +
                    " times which is more than threshold " + parameterValues[1] +
                    "  in last " + parameterValues[0] + " days");
        }
        return new EligibilityDecision(true);

    }

    private List<String> getEventList(RuleDataHolder ruleDataHolder, String eventId) throws EligibilityException {
        List<String> accountEventList = new ArrayList<>();
        try {
            accountEventList = accountEventRetriever.getAccountEvents(ruleDataHolder.getRuleParamValue(), eventId, ruleDataHolder.getHeader());
        } catch (DatatypeConfigurationException e) {
            LOGGER.info("DatatypeConfigurationException occured ", e);
        } catch (SalsaExternalServiceException | SalsaInternalServiceException | SalsaInternalResourceNotAvailableException | SalsaExternalBusinessException e) {
            throw new EligibilityException(e);
        }
        return accountEventList;
    }

    public int getMatchedEventCount(List<String> forbiddenEventList, List<String> accountEventList) {
        int count = 0;

        if (!CollectionUtils.isEmpty(accountEventList)) {
            for (String accountEvent : accountEventList) {
                for (String forbiddenEvent : forbiddenEventList) {
                    if (null != accountEvent && accountEvent.contains(forbiddenEvent)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
