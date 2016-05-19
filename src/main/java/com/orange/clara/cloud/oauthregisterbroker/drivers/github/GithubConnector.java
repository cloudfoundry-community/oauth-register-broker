package com.orange.clara.cloud.oauthregisterbroker.drivers.github;

import com.orange.clara.cloud.oauthregisterbroker.drivers.connector.BasedWebsiteConnector;
import com.orange.clara.cloud.oauthregisterbroker.exception.DriverException;
import com.orange.clara.cloud.oauthregisterbroker.exception.TwoFactorsAuthenticationException;
import com.orange.clara.cloud.oauthregisterbroker.exception.TwoFactorsAuthenticationExpiredException;
import com.orange.clara.cloud.oauthregisterbroker.model.OauthClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GithubConnector extends BasedWebsiteConnector {
    public static final String GITHUB_HOST = "https://github.com";
    public static final String GITHUB_CONNECT_FORM = GITHUB_HOST + "/session";
    public static final String GITHUB_LOGIN_PAGE = GITHUB_HOST + "/login";
    public static final String GITHUB_CONNECT_TWO_FACTORS = GITHUB_HOST + "/sessions/two-factor";
    public static final String GITHUB_REGISTER = GITHUB_HOST + "/settings/applications";
    public static final String GITHUB_REGISTER_FORM = GITHUB_HOST + "/settings/applications/new";
    public static final String GITHUB_REGISTER_EDIT_FORM = GITHUB_HOST + "/settings/applications/%s";
    public static final String APP_IDENTIFIER = "github-";
    private Logger logger = LoggerFactory.getLogger(GithubConnector.class);

    public GithubConnector() {
        super(GITHUB_HOST);
    }

    public void login(String user, String password, String authentCode) throws IOException, TwoFactorsAuthenticationException, TwoFactorsAuthenticationExpiredException {
        String loginPage = this.getPageContent(GITHUB_LOGIN_PAGE);
        String authenticityToken = this.getLoginAuthenticityToken(loginPage);
        String params = this.generateLoginParams(user, password, authenticityToken);
        HttpsURLConnection redirect = this.sendPost(GITHUB_CONNECT_FORM, params, false);

        if (redirect.getResponseCode() == 302 && redirect.getHeaderField("Location").equals(GITHUB_CONNECT_TWO_FACTORS)) {
            if (authentCode == null || authentCode.isEmpty()) {
                throw new TwoFactorsAuthenticationException();
            }
            authenticityToken = this.getLoginAuthenticityToken(this.getPageContent(GITHUB_CONNECT_TWO_FACTORS));
            HttpsURLConnection twoFactors = this.sendPost(GITHUB_CONNECT_TWO_FACTORS, this.generateTwoFactorsParams(authentCode, authenticityToken), false);
            if (twoFactors.getResponseCode() == 200) {
                throw new TwoFactorsAuthenticationExpiredException();
            }
        }
    }

    public OauthClient register(String user, String password, String authentCode, String appName, String homepageUrl, String callbackUrl) throws DriverException, IOException {
        this.login(user, password, authentCode);
        String authenticityToken = this.getRegisterAuthenticityToken(this.getPageContent(GITHUB_REGISTER_FORM));
        HttpsURLConnection registerConn = this.sendPost(GITHUB_REGISTER, this.generateRegisterAppParams(appName, homepageUrl, callbackUrl, authenticityToken), false);

        if (registerConn.getResponseCode() != 302 || registerConn.getHeaderField("Location") == null) {
            throw new DriverException("Error occured when registering on github, Response Code: " + registerConn.getResponseCode());
        }

        OauthClient oauthClient = new OauthClient();
        String urlRedirectToApp = registerConn.getHeaderField("Location");
        Document registerDoc = Jsoup.parse(this.getPageContent(urlRedirectToApp));
        oauthClient.setClientId(this.getClientId(registerDoc));
        oauthClient.setSecret(this.getClientSecret(registerDoc));

        String[] parseUrl = urlRedirectToApp.split("/");
        oauthClient.setId(APP_IDENTIFIER + parseUrl[parseUrl.length - 1]);
        return oauthClient;
    }

    public void unregister(String user, String password, String authentCode, String appId) throws DriverException, IOException {
        this.login(user, password, authentCode);
        String githubId = appId.replace(APP_IDENTIFIER, "");
        String githubAppUrl = String.format(GITHUB_REGISTER_EDIT_FORM, githubId);
        String authenticityToken = this.getUnregisterAuthenticityToken(this.getPageContent(githubAppUrl));
        HttpsURLConnection deleteConn = this.sendPost(githubAppUrl, this.generateDeleteAppParams(authenticityToken), false);
        if (deleteConn.getResponseCode() != 302) {
            throw new DriverException("Error occured when deleting app on github, Response Code: " + deleteConn.getResponseCode());
        }
    }

    private String getClientId(Document doc) {
        Elements element = doc.select(".oauth-app-info-container dd:first-of-type");
        return element.html();
    }

    private String getClientSecret(Document doc) {
        Elements element = doc.select(".oauth-app-info-container dd:last-of-type");
        return element.html();
    }

    private String generateRegisterAppParams(String appName, String homepageUrl, String callbackUrl, String authenticityToken) throws UnsupportedEncodingException {
        return String.format("%s=%s&%s=%s&%s=%s&%s=%s&authenticity_token=%s&utf8=✓",
                URLEncoder.encode("oauth_application[name]", "UTF-8"),
                URLEncoder.encode(appName, "UTF-8"),
                URLEncoder.encode("oauth_application[url]", "UTF-8"),
                URLEncoder.encode(homepageUrl, "UTF-8"),
                URLEncoder.encode("oauth_application[description]", "UTF-8"),
                URLEncoder.encode("Register by oauth-broker", "UTF-8"),
                URLEncoder.encode("oauth_application[callback_url]", "UTF-8"),
                URLEncoder.encode(callbackUrl, "UTF-8"),
                URLEncoder.encode(authenticityToken, "UTF-8")
        );
    }

    private String generateDeleteAppParams(String authenticityToken) throws UnsupportedEncodingException {
        return String.format("_method=delete&authenticity_token=%s&utf8=✓",
                URLEncoder.encode(authenticityToken, "UTF-8"),
                URLEncoder.encode("Sign in", "UTF-8")
        );
    }

    private String generateLoginParams(String user, String password, String authenticityToken) throws UnsupportedEncodingException {
        return String.format("login=%s&password=%s&authenticity_token=%s&commit=%s&utf8=✓",
                URLEncoder.encode(user, "UTF-8"),
                URLEncoder.encode(password, "UTF-8"),
                URLEncoder.encode(authenticityToken, "UTF-8"),
                URLEncoder.encode("Sign in", "UTF-8")
        );
    }

    private String generateTwoFactorsParams(String authentCode, String authenticityToken) throws UnsupportedEncodingException {
        return String.format("otp=%s&authenticity_token=%s&utf8=✓",
                URLEncoder.encode(authentCode, "UTF-8"),
                URLEncoder.encode(authenticityToken, "UTF-8")
        );
    }

    public String getRegisterAuthenticityToken(String formPage) {
        return this.getCsrfToken(formPage, ".new_oauth_application input[name='authenticity_token']");
    }

    public String getUnregisterAuthenticityToken(String formPage) {
        return this.getCsrfToken(formPage, "#cancel_info form input[name='authenticity_token']");
    }

    public String getLoginAuthenticityToken(String formPage) {
        return this.getCsrfToken(formPage, "input[name='authenticity_token']");
    }
}
