package com.lloydsbanking.salsa.ppae.web;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Routes extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("cxf:bean:processPendingArrangementEvent").doTry().to("bean:ppaeServiceProxy");
    }
}
