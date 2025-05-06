package com.lloydsbanking.salsa.eligibility.web;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Routes extends RouteBuilder {
    @Override
    public void configure() {
        //noinspection deprecation
        from("cxf:bean:determineEligibleCustomerInstructionsEndpoint").doTry().to("bean:determineEligibleCustomerInstructionsProxy");
        from("cxf:bean:determineEligibleCustomerInstructionsWZEndpoint").doTry().to("bean:determineEligibleCustomerInstructionsWZProxy");

    }
}
