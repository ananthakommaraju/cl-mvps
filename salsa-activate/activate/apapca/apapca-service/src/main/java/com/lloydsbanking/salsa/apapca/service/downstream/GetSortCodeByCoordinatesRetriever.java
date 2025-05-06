package com.lloydsbanking.salsa.apapca.service.downstream;

import com.lloydsbanking.salsa.apapca.service.convert.H071RequestFactory;
import com.lloydsbanking.salsa.downstream.fsou.client.FsOuClient;
import com.lloydstsb.ib.wsbridge.ou.StH071AGetSortCodeByCoordinates;
import com.lloydstsb.ib.wsbridge.ou.StH071BGetSortCodeByCoordinates;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.xml.ws.WebServiceException;

@Repository
public class GetSortCodeByCoordinatesRetriever {
    private static final Logger LOGGER = Logger.getLogger(GetSortCodeByCoordinatesRetriever.class);

    @Autowired
    H071RequestFactory requestFactory;

    @Autowired
    FsOuClient ouClient;

    public String getSortCode(String latitude, String longitude, RequestHeader requestHeader) {
        StH071BGetSortCodeByCoordinates h071Response = null;
        String sortCode = null;
        StH071AGetSortCodeByCoordinates h071Request = requestFactory.convert(latitude, longitude, requestHeader);
        try {
            h071Response = ouClient.getSortCodeByCoordinates(h071Request);
            if (h071Response != null && h071Response.getSterror() != null && h071Response.getSterror().getErrorno() != 0) {
                LOGGER.info("External Business Error Occurred in H071. Error Code: " + String.valueOf(h071Response.getSterror().getErrorno()));
            }
            if (h071Response!=null && !CollectionUtils.isEmpty(h071Response.getStbranchdetails())) {
                sortCode = h071Response.getStbranchdetails().get(0).getSortcode();
            }
        } catch (WebServiceException e) {
            LOGGER.info("Exception occurred(ResourceNotAvailableError) while calling H071 ", e);
        }
        return sortCode;
    }

}