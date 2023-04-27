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

    /**
     * The parameter names for <init-param> parameter names.
     */
    private static enum ParameterNames {
        allowedOrigins, // allowed origins
        allowedMethods, // allowed methods
        allowedHeaders, // allowed headers
        maxAge; // max age for the request
    }

    private static final String ORIGIN_KEY = "origin";

    // default to any / all origins
    private String allowedOrigins = "*";

    // default to all methods
    private String allowedMethods = "HEAD, GET, POST, PUT, PATCH, DELETE, OPTIONS";

    // default to all headers
    private String allowedHeaders = "Authorization, Accept, Access-Control-Request-Method, Access-Control-Request-Headers, Content-Type, Link, Origin, X-Requested-With";

    // default to 3600 seconds
    private String maxAge = "3600";

    boolean isAllowCredentials = false;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        // For security you reply to allow the origin that they specify.
        // Here we enumerate the headers to collect the origin header if any
        String origin = null;
        if (log.isTraceEnabled()) {
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                log.trace("header: {} -- {}", header, request.getHeader(header));
                if (header.equalsIgnoreCase(ORIGIN_KEY)) {
                    origin = request.getHeader(header);
                }
            }
        }
        HttpServletResponse response = (HttpServletResponse) res;
        // It they have configured to allow credentials (which makes "*" invalid),
        // then configure the allowed origin based on the origin header and the configuration.
        if (isAllowCredentials && origin != null) {
            if (allowedOrigins.contains(origin)) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Credentials", "true");
            } else {
                // the header doesn't match any configured origin: no header (which should cause CORS to fail)
                log.debug("Origin {} not among allowedOrigins: {}", origin, allowedOrigins);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
                return;
            }
        } else {
            response.setHeader("Access-Control-Allow-Origin", "*");
        }
        // check methods
        if (allowedMethods.contains(request.getMethod())) {
            // method request is allowed
        } else {
            log.warn("Method {} not among allowedMethods: {}", request.getMethod(), allowedMethods);
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED); // 405
            return;
        }
        response.setHeader("Access-Control-Allow-Methods", allowedMethods);
        // enforce header checks?
        response.setHeader("Access-Control-Allow-Headers", allowedHeaders);
        response.setHeader("Access-Control-Max-Age", maxAge);
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

}
