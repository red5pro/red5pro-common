package com.red5pro.servlet.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Red5 Pro CORS filter implementation.
 *
 * @author Nate Roe
 * @author Paul Gregoire
 */
public class CorsFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(CorsFilter.class);

    private static boolean isDebug = log.isDebugEnabled();

    /**
     * The parameter names for <init-param> parameter names.
     */
    private static enum ParameterNames {
        exposeAllHeaders, // expose all headers
        allowedOrigins, // allowed origins
        allowedMethods, // allowed methods
        allowedHeaders, // allowed headers
        maxAge; // max age for the request
    }

    private static enum CorsResponseHeaderNames {
        allowOrigin("Access-Control-Allow-Origin"), allowCredentials("Access-Control-Allow-Credentials"), allowMethods("Access-Control-Allow-Methods"), allowHeaders("Access-Control-Allow-Headers"), exposeHeaders("Access-Control-Expose-Headers"), maxAge("Access-Control-Max-Age");

        private String headerName;

        private CorsResponseHeaderNames(String headerName) {
            this.headerName = headerName;
        }

        public String getHeaderName() {
            return headerName;
        }

        public String getHeaderNameLC() {
            return headerName.toLowerCase();
        }

    }

    private static final String ORIGIN_KEY = "origin";

    // default to any / all origins
    private String allowedOrigins = "*";

    // default to all methods
    private String allowedMethods = "HEAD, GET, POST, PUT, PATCH, DELETE, OPTIONS";

    // default to all headers
    // XXX(paul) removed Access-Control-Request-Method, Access-Control-Request-Headers, as they are from the requesting browser
    private String allowedHeaders = "Authorization, Accept, Content-Type, Link, Location, Origin, X-Requested-With";

    // expose all headers
    private boolean exposeAllHeaders = true;

    // default to 3600 seconds
    private String maxAge = "3600";

    boolean isAllowCredentials = false;

    // whether to use lowercase headers or not; determined by what arrives in the request
    private boolean useLowerCaseHeaders = false;

    /*
    https://fetch.spec.whatwg.org/#http-cors-protocol

        Access-Control-Request-Method    = method
        Access-Control-Request-Headers   = 1#field-name

        wildcard                         = "*"
        Access-Control-Allow-Origin      = origin-or-null / wildcard
        Access-Control-Allow-Credentials = %s"true" ; case-sensitive
        Access-Control-Expose-Headers    = #field-name
        Access-Control-Max-Age           = delta-seconds
        Access-Control-Allow-Methods     = #method
        Access-Control-Allow-Headers     = #field-name

        For `Access-Control-Expose-Headers`, `Access-Control-Allow-Methods`, and `Access-Control-Allow-Headers`
        response headers, the value `*` counts as a wildcard for requests without credentials. For such requests there
        is no way to solely match a header name or method that is `*`.
    */

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if (isDebug) {
            log.debug("Method in-filter: {}", request.getMethod());
            debugDump(request, response);
        }
        // For security you reply to allow the origin that they specify.
        // Here we enumerate the headers to collect the origin header if any
        String origin = request.getHeader(ORIGIN_KEY);
        // determine case by origin header; if lowercase, response headers will be lowercase
        if (origin != null) {
            useLowerCaseHeaders = true;
        } else {
            origin = request.getHeader("Origin");
        }
        if (isDebug) {
            log.debug("Origin: {}", origin);
        }
        // It they have configured to allow credentials (which makes "*" invalid),
        // then configure the allowed origin based on the origin header and the configuration.
        if (isAllowCredentials && origin != null) {
            if ("*".equals(allowedOrigins) || allowedOrigins.contains(origin)) {
                response.setHeader((useLowerCaseHeaders ? CorsResponseHeaderNames.allowOrigin.getHeaderNameLC() : CorsResponseHeaderNames.allowOrigin.getHeaderName()), origin);
                response.setHeader((useLowerCaseHeaders ? CorsResponseHeaderNames.allowCredentials.getHeaderNameLC() : CorsResponseHeaderNames.allowCredentials.getHeaderName()), "true");
            } else {
                // the header doesn't match any configured origin: no header (which should cause CORS to fail)
                log.debug("Origin {} not among allowedOrigins: {}", origin, allowedOrigins);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
                return;
            }
        } else {
            response.setHeader((useLowerCaseHeaders ? CorsResponseHeaderNames.allowOrigin.getHeaderNameLC() : CorsResponseHeaderNames.allowOrigin.getHeaderName()), "*");
        }
        // check methods
        if (allowedMethods.contains(request.getMethod())) {
            // method request is allowed
        } else {
            log.warn("Method {} not among allowedMethods: {}", request.getMethod(), allowedMethods);
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED); // 405
            return;
        }
        // allowed methods
        response.setHeader((useLowerCaseHeaders ? CorsResponseHeaderNames.allowMethods.getHeaderNameLC() : CorsResponseHeaderNames.allowMethods.getHeaderName()), allowedMethods);
        // expose headers (via config)
        if (exposeAllHeaders) {
            // authorization is a special case
            response.setHeader((useLowerCaseHeaders ? CorsResponseHeaderNames.exposeHeaders.getHeaderNameLC() : CorsResponseHeaderNames.exposeHeaders.getHeaderName()), "*, Authorization");
        }
        // enforce header checks
        if (request.getHeaders("access-control-request-headers") != null || request.getHeaders("Access-Control-Request-Headers") != null) {
            response.setHeader((useLowerCaseHeaders ? CorsResponseHeaderNames.allowHeaders.getHeaderNameLC() : CorsResponseHeaderNames.allowHeaders.getHeaderName()), allowedHeaders);
        }
        // max age
        response.setHeader((useLowerCaseHeaders ? CorsResponseHeaderNames.maxAge.getHeaderNameLC() : CorsResponseHeaderNames.maxAge.getHeaderName()), maxAge);
        // hand off to the next filter if we've made it this far
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Enumeration<String> params = filterConfig.getInitParameterNames();
        params.asIterator().forEachRemaining(paramName -> {
            log.debug("param: {}", paramName);
            ParameterNames param = ParameterNames.valueOf(paramName);
            switch (param) {
                case exposeAllHeaders:
                    exposeAllHeaders = Boolean.valueOf(filterConfig.getInitParameter(paramName));
                    break;
                case allowedOrigins:
                    allowedOrigins = filterConfig.getInitParameter(paramName);
                    isAllowCredentials = true;
                    break;
                case allowedMethods:
                    allowedMethods = filterConfig.getInitParameter(paramName);
                    break;
                case allowedHeaders:
                    allowedHeaders = filterConfig.getInitParameter(paramName);
                    break;
                case maxAge:
                    maxAge = filterConfig.getInitParameter(paramName);
                    break;
            }
        });
    }

    @Override
    public void destroy() {
    }

    /**
     * Debugging info for request/response.
     *
     * @param request
     * @param response
     */
    protected void debugDump(HttpServletRequest request, HttpServletResponse response) {
        if (isDebug) {
            // dump the headers
            request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
                log.debug("Header {}: {}", headerName, request.getHeader(headerName));
            });
            request.getParameterNames().asIterator().forEachRemaining(paramName -> {
                log.debug("Parameter {}: {}", paramName, request.getParameter(paramName));
            });
            request.getAttributeNames().asIterator().forEachRemaining(attrName -> {
                log.debug("Attribute {}: {}", attrName, request.getAttribute(attrName));
            });
        }
    }

}
