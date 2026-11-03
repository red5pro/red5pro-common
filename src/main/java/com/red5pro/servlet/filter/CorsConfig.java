package com.red5pro.servlet.filter;

/**
 * Configuration for the CORS servlet filter: allowed origins, methods, headers, credentials,
 * and the cross-origin isolation headers used by WebTransport.
 */
public class CorsConfig {

    /**
     * Creates a configuration with default values.
     */
    public CorsConfig() {
    }

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

    /**
     * Sets the allowed origins.
     *
     * @param allowedOrigins the allowed origins
     */
    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    /**
     * Sets the allowed methods.
     *
     * @param allowedMethods the allowed methods
     */
    public void setAllowedMethods(String allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    /**
     * Sets the allowed headers.
     *
     * @param allowedHeaders the allowed headers
     */
    public void setAllowedHeaders(String allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    /**
     * Sets whether all headers are exposed.
     *
     * @param exposeAllHeaders true to expose all headers
     */
    public void setExposeAllHeaders(boolean exposeAllHeaders) {
        this.exposeAllHeaders = exposeAllHeaders;
    }

    /**
     * Sets the pre-flight max age in seconds.
     *
     * @param maxAge the max age in seconds
     */
    public void setMaxAge(String maxAge) {
        this.maxAge = maxAge;
    }

    /**
     * Sets whether credentials are allowed.
     *
     * @param allowCredentials true to allow credentials
     */
    public void setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    /**
     * Returns whether lowercase headers are used.
     *
     * @return true if lowercase headers are used
     */
    public boolean isUseLowerCaseHeaders() {
        return useLowerCaseHeaders;
    }

    /**
     * Sets whether lowercase headers are used.
     *
     * @param useLowerCaseHeaders true to use lowercase headers
     */
    public void setUseLowerCaseHeaders(boolean useLowerCaseHeaders) {
        this.useLowerCaseHeaders = useLowerCaseHeaders;
    }

    /**
     * Returns the allowed origins.
     *
     * @return the allowed origins
     */
    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    /**
     * Returns the allowed methods.
     *
     * @return the allowed methods
     */
    public String getAllowedMethods() {
        return allowedMethods;
    }

    /**
     * Returns the allowed headers.
     *
     * @return the allowed headers
     */
    public String getAllowedHeaders() {
        return allowedHeaders;
    }

    /**
     * Returns whether all headers are exposed.
     *
     * @return true if all headers are exposed
     */
    public boolean isExposeAllHeaders() {
        return exposeAllHeaders;
    }

    /**
     * Returns the pre-flight max age in seconds.
     *
     * @return the max age
     */
    public String getMaxAge() {
        return maxAge;
    }

    /**
     * Returns whether credentials are allowed.
     *
     * @return true if credentials are allowed
     */
    public boolean isAllowCredentials() {
        return allowCredentials;
    }

    /**
     * Returns whether a successful pre-flight returns immediately instead of continuing the filter chain.
     *
     * @return true if pre-flight short-circuits
     */
    public boolean isOptionsShortCircuit() {
        return isOptionsShortCircuit;
    }

    /**
     * Sets whether a successful pre-flight returns immediately instead of continuing the filter chain.
     *
     * @param isOptionsShortCircuit true to short-circuit
     */
    public void setOptionsShortCircuit(boolean isOptionsShortCircuit) {
        this.isOptionsShortCircuit = isOptionsShortCircuit;
    }

    /**
     * Returns the Cross-Origin-Embedder-Policy value.
     *
     * @return the COEP value
     */
    public String getCoepValue() {
        return coepValue;
    }

    /**
     * Sets the Cross-Origin-Embedder-Policy value.
     *
     * @param coepValue the COEP value
     */
    public void setCoepValue(String coepValue) {
        this.coepValue = coepValue;
    }

    /**
     * Returns the Cross-Origin-Opener-Policy value.
     *
     * @return the COOP value
     */
    public String getCoopValue() {
        return coopValue;
    }

    /**
     * Sets the Cross-Origin-Opener-Policy value.
     *
     * @param coopValue the COOP value
     */
    public void setCoopValue(String coopValue) {
        this.coopValue = coopValue;
    }

    /**
     * Returns the Cross-Origin-Resource-Policy value.
     *
     * @return the CORP value
     */
    public String getCorpValue() {
        return corpValue;
    }

    /**
     * Sets the Cross-Origin-Resource-Policy value.
     *
     * @param corpValue the CORP value
     */
    public void setCorpValue(String corpValue) {
        this.corpValue = corpValue;
    }

    @Override
    public String toString() {
        return "CorsConfig [allowedOrigins=" + allowedOrigins + ", allowedMethods=" + allowedMethods + ", allowedHeaders=" + allowedHeaders + ", exposeAllHeaders=" + exposeAllHeaders + ", maxAge=" + maxAge + ", allowCredentials=" + allowCredentials + ", useLowerCaseHeaders=" + useLowerCaseHeaders + ", isOptionsShortCircuit=" + isOptionsShortCircuit + ", coepValue=" + coepValue + ", coopValue=" + coopValue
                + ", corpValue=" + corpValue + "]";
    }
}
