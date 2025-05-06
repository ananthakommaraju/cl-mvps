package com.lloydsbanking.salsa.aps.web;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Routes extends RouteBuilder {
    @Override
    public void configure() {
        from("cxf:bean:administerProductArrangementEndPoint").doTry().to("bean:apsServiceProxy");
    }
}
