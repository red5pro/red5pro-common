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

    @Override
    public String toString() {
        return "CorsConfig [allowedOrigins=" + allowedOrigins + ", allowedMethods=" + allowedMethods + ", allowedHeaders=" + allowedHeaders + ", exposeAllHeaders=" + exposeAllHeaders + ", maxAge=" + maxAge + ", isAllowCredentials=" + allowCredentials + ", useLowerCaseHeaders=" + useLowerCaseHeaders + "]";
    }

}
