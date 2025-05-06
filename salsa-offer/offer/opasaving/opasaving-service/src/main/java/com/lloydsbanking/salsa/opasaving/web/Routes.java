package com.lloydsbanking.salsa.opasaving.web;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Routes extends RouteBuilder {
    @Override
    public void configure() {
        from("cxf:bean:offerProductArrangementsavingEndpoint").doTry().to("bean:opaSavingServiceProxy");
    }
}
