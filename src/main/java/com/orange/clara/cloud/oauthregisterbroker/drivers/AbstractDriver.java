package com.orange.clara.cloud.oauthregisterbroker.drivers;

import com.google.common.collect.Lists;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Copyright (C) 2016 Orange
 * <p>
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE' in this package distribution
 * or at 'https://opensource.org/licenses/Apache-2.0'.
 * <p>
 * Author: Arthur Halet
 * Date: 15/05/2016
 */
public abstract class AbstractDriver implements Driver {
    @Value("${skip.ssl.verification:false}")
    public boolean skipSslVerification;

    protected String createClientId(CloudApplication app) {
        return app.getName() + "-" + this.randomString(15);
    }

    protected String createClientPassword() {
        return this.randomString(15);
    }

    protected String randomString(int number) {
        RandomValueStringGenerator random = new RandomValueStringGenerator(number);
        return random.generate();
    }

    protected List<String> getVerifiedUris(List<String> uris) {
        List<String> verifiedUris = Lists.newArrayList();
        for (String uri : uris) {
            if (this.urlHttpsUriExist(uri)) {
                verifiedUris.add("https://" + uri);
            }
            if (this.urlHttpUriExist(uri)) {
                verifiedUris.add("http://" + uri);
            }
        }
        return verifiedUris;
    }

    protected boolean urlHttpUriExist(String uri) {
        try {
            URL website = new URL("http://" + uri);
            HttpURLConnection con = (HttpURLConnection) website.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", "OauthRegister");

            int responseCode = con.getResponseCode();
            if (responseCode == 404) {
                return false;
            }
            return true;
        } catch (IOException e) {
        }

        return false;
    }

    protected boolean urlHttpsUriExist(String uri) {
        try {
            URL website = new URL("https://" + uri);
            HttpsURLConnection con = (HttpsURLConnection) website.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");
            if (this.skipSslVerification) {
                con.setHostnameVerifier((hostname, session) -> true);
            }
            //add request header
            con.setRequestProperty("User-Agent", "OauthRegister");

            int responseCode = con.getResponseCode();
            if (responseCode == 404) {
                return false;
            }
            return true;
        } catch (IOException e) {
        }

        return false;
    }
}
