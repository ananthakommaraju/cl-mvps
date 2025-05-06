package com.lloydsbanking.salsa.apacc.web;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Routes extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("cxf:bean:activateProductArrangementCcEndpoint").doTry().to("bean:apaCcServiceProxy");
    }
}
