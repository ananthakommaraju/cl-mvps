package com.lloydsbanking.salsa.eligibility.logging.wz;

import com.lloydsbanking.salsa.logging.LogService;
import lib_sim_gmo.messages.RequestHeader;
import org.springframework.beans.factory.annotation.Value;


public class EligibilityLogService extends LogService {
    @Value("${clone.name}")
    protected String cloneName;

    public void initialiseContext(RequestHeader requestHeader) {
        super.initialiseContext(requestHeader, "Salsa EligibilityWZ", cloneName, "eligibilityWZ");
    }

}
