package com.lloydsbanking.salsa.eligibility.logging;

import com.lloydsbanking.salsa.logging.LogService;
import lb_gbo_sales.messages.RequestHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EligibilityLogService extends LogService {
    @Value("${clone.name}")
    protected String cloneName;

    public void initialiseContext(RequestHeader requestHeader) {
        super.initialiseContext(requestHeader, "Salsa Eligibility", cloneName, "eligibility");
    }

}
