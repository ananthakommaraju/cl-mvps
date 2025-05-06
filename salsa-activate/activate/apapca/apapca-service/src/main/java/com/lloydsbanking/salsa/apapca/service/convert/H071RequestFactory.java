package com.lloydsbanking.salsa.apapca.service.convert;

import com.lloydsbanking.salsa.brand.Brand;
import com.lloydsbanking.salsa.brand.Channel;
import com.lloydsbanking.salsa.downstream.fsou.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fs.ou.StHeader;
import com.lloydstsb.ib.wsbridge.ou.StH071AGetSortCodeByCoordinates;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class H071RequestFactory {
    public static final String DISTANCE_IN_MILES = "50";

    public static final String MAX_SEARCH_RESULT = "1";

    @Autowired
    BapiHeaderToStHeaderConverter bapiHeaderToStHeaderConverter;

    @Autowired
    HeaderRetriever headerRetriever;

    public StH071AGetSortCodeByCoordinates convert(String latitude, String longitude, RequestHeader header) {
        StH071AGetSortCodeByCoordinates request = new StH071AGetSortCodeByCoordinates();
        BigInteger ocisId = null;
        StHeader stHeader = getStHeader(header, ocisId);
        request.setStheader(stHeader);
        if (!StringUtils.isEmpty(latitude)) {
            request.setSearchoriginLatitude(latitude);
        }
        if (!StringUtils.isEmpty(longitude)) {
            request.setSearchoriginLongitude(longitude);
        }
        if (!StringUtils.isEmpty(latitude) && !StringUtils.isEmpty(longitude)) {
            request.setSearchrangeInMiles(DISTANCE_IN_MILES);
            request.setBranchfeaturedescription("Accounting");
        } else {
            request.setBranchfeaturedescription("Default");
        }
        request.setSearchResultsMax(MAX_SEARCH_RESULT);
        request.setBranchfeaturedescriptionDefault("Default");
        return request;
    }

    private StHeader getStHeader(RequestHeader requestHeader, BigInteger ocisId) {
        BAPIHeader bapiHeader = headerRetriever.getBapiInformationHeader(requestHeader).getBAPIHeader();
        String contactPointId = headerRetriever.getContactPoint(requestHeader).getContactPointId();
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader);
        Brand brand = Channel.getBrandForChannel(Channel.fromString(bapiHeader.getChanid()));
        return bapiHeaderToStHeaderConverter.convert(bapiHeader, serviceRequest, contactPointId, ocisId, brand);
    }

}
