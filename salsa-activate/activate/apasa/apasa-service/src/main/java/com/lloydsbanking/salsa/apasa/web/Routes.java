package com.lloydsbanking.salsa.apasa.web;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Routes extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("cxf:bean:activateProductArrangementSaEndpoint").doTry().to("bean:apaSaServiceProxy");
    }
}
