package com.lloydsbanking.salsa.opacc.web;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Routes extends RouteBuilder {
    @Override
    public void configure() {
        from("cxf:bean:offerProductArrangementPccEndpoint").doTry().to("bean:opaccServiceProxy");
    }
}
