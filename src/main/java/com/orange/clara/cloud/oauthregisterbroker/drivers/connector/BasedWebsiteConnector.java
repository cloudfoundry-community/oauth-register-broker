package com.orange.clara.cloud.oauthregisterbroker.drivers.connector;

import com.google.common.collect.Lists;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;

/**
 * Copyright (C) 2016 Orange
 * <p>
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE' in this package distribution
 * or at 'https://opensource.org/licenses/Apache-2.0'.
 * <p>
 * Author: Arthur Halet
 * Date: 19/05/2016
 */
public abstract class BasedWebsiteConnector {

    private static final String CCNG_API_PROXY_HOST = System.getProperty("http.proxyHost", null);

    private static final String CCNG_API_PROXY_PASSWD = System.getProperty("http.proxyPassword", null);

    private static final int CCNG_API_PROXY_PORT = Integer.getInteger("http.proxyPort", 80);

    private static final String CCNG_API_PROXY_USER = System.getProperty("http.proxyUsername", null);

    private final String USER_AGENT = "Mozilla/5.0";
    private String acceptLangageHeader = "en-US,en;q=0.5";
    private String acceptHeader = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private CookieManager cookieManager;
    private Logger logger = LoggerFactory.getLogger(BasedWebsiteConnector.class);
    private String websiteHomepage;
    private Proxy proxy;

    public BasedWebsiteConnector(String websiteHomepage) {
        this.websiteHomepage = websiteHomepage;
        cookieManager = new CookieManager();
    }

    protected HttpsURLConnection sendPost(String url, String postParams) throws IOException {
        return this.sendPost(url, postParams, true);
    }

    protected HttpsURLConnection sendPost(String url, String postParams, boolean followRedirect) throws IOException {

        URL obj = new URL(url);
        HttpsURLConnection conn = null;

        if (this.proxy != null) {
            conn = (HttpsURLConnection) obj.openConnection(this.proxy);
        } else {
            conn = (HttpsURLConnection) obj.openConnection();
        }
        // Acts like a browser
        conn.setInstanceFollowRedirects(followRedirect);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Host", "github.com");
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept", acceptHeader);
        conn.setRequestProperty("Accept-Language", acceptLangageHeader);
        this.loadCookies(conn);
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Referer", websiteHomepage);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

        conn.setDoOutput(true);
        conn.setDoInput(true);

        // Send post request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        logger.debug("Sending 'POST' request to URL : " + url);
        logger.debug("Post parameters : " + postParams);
        logger.debug("Response Code : " + responseCode);
        this.saveCookies(conn);

        return conn;
    }

    protected String getPageContent(String url) throws IOException {

        URL obj = new URL(url);
        HttpsURLConnection conn = null;
        if (this.proxy != null) {
            conn = (HttpsURLConnection) obj.openConnection(this.proxy);
        } else {
            conn = (HttpsURLConnection) obj.openConnection();
        }


        // default is GET
        conn.setRequestMethod("GET");

        conn.setUseCaches(false);
        this.loadCookies(conn);
        // act like a browser
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept", acceptHeader);
        conn.setRequestProperty("Accept-Language", acceptLangageHeader);

        int responseCode = conn.getResponseCode();

        logger.debug("Sending 'GET' request to URL : " + url);
        logger.debug("Response Code : " + responseCode);
        this.saveCookies(conn);

        return this.parseResponse(conn);

    }

    protected void saveCookies(HttpsURLConnection conn) {
        List<String> cookiesHeader = conn.getHeaderFields().get("Set-Cookie");
        if (cookiesHeader == null) {
            return;
        }
        for (String cookie : cookiesHeader) {
            if (cookie.isEmpty()) {
                continue;
            }

            for (HttpCookie httpCookie : HttpCookie.parse(cookie)) {
                httpCookie.setMaxAge(-1);
                cookieManager.getCookieStore().add(null, httpCookie);
            }
        }
    }

    protected void loadCookies(HttpsURLConnection conn) {

        if (cookieManager.getCookieStore().getCookies().size() == 0) {
            return;
        }
        List<String> cookiesInString = Lists.newArrayList();
        for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {
            cookiesInString.add(cookie.toString());
        }
        conn.setRequestProperty("Cookie", String.join("; ", cookiesInString));

    }

    public String parseResponse(HttpsURLConnection conn) throws IOException {
        BufferedReader in =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    public String getCsrfToken(String formPage, String selector) {
        Document doc = Jsoup.parse(formPage);
        Elements authenticityToken = doc.select(selector);
        return authenticityToken.val();
    }

    public String getWebsiteHomepage() {
        return websiteHomepage;
    }

    public void setWebsiteHomepage(String websiteHomepage) {
        this.websiteHomepage = websiteHomepage;
    }

    public String getAcceptLangageHeader() {
        return acceptLangageHeader;
    }

    public void setAcceptLangageHeader(String acceptLangageHeader) {
        this.acceptLangageHeader = acceptLangageHeader;
    }

    public String getAcceptHeader() {
        return acceptHeader;
    }

    public void setAcceptHeader(String acceptHeader) {
        this.acceptHeader = acceptHeader;
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    public void setCookieManager(CookieManager cookieManager) {
        this.cookieManager = cookieManager;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    @PostConstruct
    public void createProxyIfExists() throws Exception {
        if (CCNG_API_PROXY_HOST == null) {
            return;
        }
        if (CCNG_API_PROXY_USER != null) {
            Authenticator authenticator = new Authenticator() {

                public PasswordAuthentication getPasswordAuthentication() {
                    return (new PasswordAuthentication(CCNG_API_PROXY_USER,
                            CCNG_API_PROXY_PASSWD.toCharArray()));
                }
            };
            Authenticator.setDefault(authenticator);
        }
        this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(CCNG_API_PROXY_HOST, CCNG_API_PROXY_PORT));
    }
}
