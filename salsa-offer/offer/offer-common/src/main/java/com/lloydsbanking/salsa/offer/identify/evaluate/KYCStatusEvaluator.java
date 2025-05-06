package com.lloydsbanking.salsa.offer.identify.evaluate;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.PartyEnqData;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.Product;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.List;

public class KYCStatusEvaluator {

    @Autowired
    DateFactory dateFactory;

    private static final Logger LOGGER = Logger.getLogger(KYCStatusEvaluator.class);

    private static final String PRODUCT_STATUS_ACTIVE = "001";


    public boolean isKycCompliant(Customer customer, List<Product> productHoldings, PartyEnqData partyEnqData) {
        LOGGER.info("Checking Customer's KYC Compliance");
        if (null != partyEnqData && null != partyEnqData.getPersonalData() && null != partyEnqData.getPersonalData().getBirthDt() && !CollectionUtils.isEmpty(productHoldings)) {
            if (getKycCode(partyEnqData) && isValidHoldingExists(productHoldings) && isCustomerDobMatchesDobOnOcis(partyEnqData, customer.getIsPlayedBy().getBirthDate())) {
                LOGGER.info("Customer is KYC Compliant");
                return true;
            }
        } else {
            boolean kycCompliance = getKycCode(partyEnqData);
            LOGGER.info("Is Customer KYC Compliant: " + kycCompliance);
            return kycCompliance;
        }
        LOGGER.info("Customer is not KYC Compliant");
        return false;
    }

    private boolean getKycCode(PartyEnqData partyEnqData) {
        if (null != partyEnqData && null != partyEnqData.getEvidenceData() && !partyEnqData.getEvidenceData().getPartyEvid().isEmpty() && !partyEnqData.getEvidenceData().getAddrEvid().isEmpty()) {
            LOGGER.info("Evidence Data from OCIS is available");
            return true;
        }
        LOGGER.info("Evidence Data from OCIS is not available");
        return false;
    }

    private boolean isValidHoldingExists(List<Product> productHoldings) {
        for (Product productHolding : productHoldings) {
            if (PRODUCT_STATUS_ACTIVE.equals(productHolding.getStatusCode())) {
                LOGGER.info("Valid product holdings exist");
                return true;
            }
        }
        LOGGER.info("None of the product holdings are valid");
        return false;
    }

    private boolean isCustomerDobMatchesDobOnOcis(PartyEnqData partyEnqData, XMLGregorianCalendar customerRequestDOB) {
        if (null != customerRequestDOB && !StringUtils.isEmpty(partyEnqData.getPersonalData().getBirthDt())) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            XMLGregorianCalendar customerOcisDOB = dateFactory.stringToXMLGregorianCalendar(dateFactory.getDatePattern(partyEnqData.getPersonalData().getBirthDt(), "-"), dateFormat);
            LOGGER.info("DOB on OCIS: " + customerOcisDOB + " and DOB in Req: " + customerRequestDOB);
            long compareDates = dateFactory.differenceInDays(customerOcisDOB.toGregorianCalendar().getTime(), customerRequestDOB.toGregorianCalendar().getTime());
            if (compareDates == 0) {
                LOGGER.info("DOB on OCIS matches with DOB passed in request");
                return true;
            }
        }
        LOGGER.info("DOB on OCIS does not match with DOB passed in request");
        return false;
    }

}
