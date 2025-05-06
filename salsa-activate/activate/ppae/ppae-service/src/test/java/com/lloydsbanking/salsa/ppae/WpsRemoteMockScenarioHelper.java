package com.lloydsbanking.salsa.ppae;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.Rollback;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class WpsRemoteMockScenarioHelper extends RemoteMockScenarioHelper {

    @Value("${wps.cache.url}")
    String wpsCacheUrl;

    @Override
    @Rollback(false)
    public void clearUpForWPS() {
        clearWpsCache();
    }

    public void clearWpsCache() {
        try {
            URL wpsCache = new URL(wpsCacheUrl);
            URLConnection uc = wpsCache.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            in.close();
        } catch (Exception e) {

        }
    }
    public void sleep() {
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
