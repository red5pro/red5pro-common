package com.red5pro.servlet.filter;

public class CorsConfig {

    // default to any / all origins
    private String allowedOrigins = "*";

    // default to all methods
    private String allowedMethods = "HEAD, GET, POST, PUT, PATCH, DELETE, OPTIONS";

    // default to all headers
    private String allowedHeaders = "Authorization, Accept, Content-Type, Link, Location, Origin, X-Requested-With";

    // expose all headers
    private boolean exposeAllHeaders = true;

    // default to 3600 seconds
    private String maxAge = "3600";

    private boolean allowCredentials;

    // whether to use lowercase headers or not; determined by what arrives in the request
    private boolean useLowerCaseHeaders;

    // true if successful CORS pre-flight should short-circuit (return immediately)
    // if false, will still call filter chain
    // default is false to match pre-existing behavior
    // (if you don't add new config explicitly setting this true, then there is no behavior change)
    private boolean isOptionsShortCircuit = false;

    /*
        Security headers for WebTransport
            Cross-Origin-Opener-Policy "same-origin"
            Cross-Origin-Embedder-Policy "require-corp"
            Cross-Origin-Resource-Policy "cross-origin"

            <property name="coepValue" value="require-corp" />
            <property name="coopValue" value="same-origin" />
            <property name="corpValue" value="cross-origin" />

    */
    private String coepValue = "require-corp";

    private String coopValue = "same-origin";

    private String corpValue = "cross-origin";

    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public void setAllowedMethods(String allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public void setAllowedHeaders(String allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    public void setExposeAllHeaders(boolean exposeAllHeaders) {
        this.exposeAllHeaders = exposeAllHeaders;
    }

    public void setMaxAge(String maxAge) {
        this.maxAge = maxAge;
    }

    public void setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public boolean isUseLowerCaseHeaders() {
        return useLowerCaseHeaders;
    }

    public void setUseLowerCaseHeaders(boolean useLowerCaseHeaders) {
        this.useLowerCaseHeaders = useLowerCaseHeaders;
    }

    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    public String getAllowedMethods() {
        return allowedMethods;
    }

    public String getAllowedHeaders() {
        return allowedHeaders;
    }

    public boolean isExposeAllHeaders() {
        return exposeAllHeaders;
    }

    public String getMaxAge() {
        return maxAge;
    }

    public boolean isAllowCredentials() {
        return allowCredentials;
    }

    public boolean isOptionsShortCircuit() {
        return isOptionsShortCircuit;
    }

    public void setOptionsShortCircuit(boolean isOptionsShortCircuit) {
        this.isOptionsShortCircuit = isOptionsShortCircuit;
    }

    public String getCoepValue() {
        return coepValue;
    }

    public void setCoepValue(String coepValue) {
        this.coepValue = coepValue;
    }

    public String getCoopValue() {
        return coopValue;
    }

    public void setCoopValue(String coopValue) {
        this.coopValue = coopValue;
    }

    public String getCorpValue() {
        return corpValue;
    }

    public void setCorpValue(String corpValue) {
        this.corpValue = corpValue;
    }

    @Override
    public String toString() {
        return "CorsConfig [allowedOrigins=" + allowedOrigins + ", allowedMethods=" + allowedMethods + ", allowedHeaders=" + allowedHeaders + ", exposeAllHeaders=" + exposeAllHeaders + ", maxAge=" + maxAge + ", allowCredentials=" + allowCredentials + ", useLowerCaseHeaders=" + useLowerCaseHeaders + ", isOptionsShortCircuit=" + isOptionsShortCircuit + ", coepValue=" + coepValue + ", coopValue=" + coopValue
                + ", corpValue=" + corpValue + "]";
    }
}
