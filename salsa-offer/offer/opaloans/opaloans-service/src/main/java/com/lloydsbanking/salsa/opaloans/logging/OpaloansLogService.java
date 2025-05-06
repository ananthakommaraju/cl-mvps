package com.lloydsbanking.salsa.opaloans.logging;

import com.lloydsbanking.salsa.logging.LogService;
import lib_sim_gmo.messages.RequestHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpaloansLogService extends LogService {
    @Value("${clone.name}")
    protected String cloneName;

    public void initialiseContext(RequestHeader requestHeader) {
        super.initialiseContext(requestHeader, "Salsa Opaloans", cloneName, "offerProductArrangement");
    }


}
