package com.lloydsbanking.salsa.opaloans.web;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Routes extends RouteBuilder {
    @Override
    public void configure() {
        from("cxf:bean:offerProductArrangementLoansEndpoint").doTry().to("bean:opaLoansServiceProxy");
    }
}
