package com.red5pro.servlet.filter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

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
        useLowerCaseHeaders, // use lowercase headers
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

    // filter configuation from initialization
    private FilterConfig filterConfig;

    // configuration
    private CorsConfig corsConfig;

    private AtomicBoolean initialized = new AtomicBoolean(false);

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
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    /**
     * Initialize the filter; not be confused with the servlet init method. This method fires on the first request.
     * Otherwise we're at the mercy of the server loading the filter on startup and being configurable via the Red5ProPlugin.
     *
     * @param filterConfig
     * @throws ServletException
     */
    public void doInit() throws ServletException {
        log.debug("Initializing");
        if (filterConfig != null) {
            log.debug("Filter config: {}", filterConfig);
            // start with a default config
            corsConfig = new CorsConfig();
            // let the user override the defaults
            Enumeration<String> params = filterConfig.getInitParameterNames();
            if (params != null) {
                log.debug("Found {} init parameters", params);
                params.asIterator().forEachRemaining(paramName -> {
                    log.debug("param: {}", paramName);
                    ParameterNames param = ParameterNames.valueOf(paramName);
                    switch (param) {
                        case exposeAllHeaders:
                            corsConfig.setExposeAllHeaders(Boolean.valueOf(filterConfig.getInitParameter(paramName)));
                            break;
                        case allowedOrigins:
                            corsConfig.setAllowedOrigins(filterConfig.getInitParameter(paramName));
                            corsConfig.setAllowCredentials(true);
                            break;
                        case allowedMethods:
                            corsConfig.setAllowedMethods(filterConfig.getInitParameter(paramName));
                            break;
                        case allowedHeaders:
                            corsConfig.setAllowedHeaders(filterConfig.getInitParameter(paramName));
                            break;
                        case maxAge:
                            corsConfig.setMaxAge(filterConfig.getInitParameter(paramName));
                            break;
                        case useLowerCaseHeaders:
                            corsConfig.setUseLowerCaseHeaders(Boolean.valueOf(filterConfig.getInitParameter(paramName)));
                            break;
                        default:
                            log.warn("Unknown parameter: {}", paramName);
                            break;
                    }
                });
            } else {
                log.debug("No init parameters found in web.xml, global will be used, in lieu of an application configuration");
                // check for central config in the context
                ApplicationContext appCtx = (ApplicationContext) filterConfig.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
                // if theres no context then this is not running in a red5 app
                if (appCtx == null) {
                    log.debug("No application context found");
                    // attempt to read from Red5ProPlugin via reflection if theres no context
                    try {
                        Class<?> clazz = Class.forName("com.red5pro.plugin.Red5ProPlugin");
                        Method method = clazz.getMethod("getCorsConfig");
                        CorsConfig tmp = (CorsConfig) method.invoke(null, new Object[0]);
                        if (tmp != null) {
                            corsConfig = tmp;
                        }
                    } catch (Exception e) {
                        log.warn("Could not read corsConfig from Red5ProPlugin", e);
                    }
                } else if (appCtx.containsBean("web.handler")) {
                    MultiThreadedApplicationAdapter app = (MultiThreadedApplicationAdapter) appCtx.getBean("web.handler");
                    CorsConfig tmp = (CorsConfig) app.getAttribute("corsConfig");
                    if (tmp != null) {
                        corsConfig = tmp;
                    }
                }
            }
            log.info("Cors config: {}", corsConfig);
        } else {
            log.warn("Filter config is null");
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if (isDebug) {
            log.debug("Method in-filter: {}", request.getMethod());
            debugDump(request, response);
        }
        // ensure we've been initialized as expected
        if (initialized.compareAndSet(false, true)) {
            doInit();
        } else if (corsConfig == null) {
            log.warn("Not initialized properly");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            return;
        }
        // For security you reply to allow the origin that they specify.
        // Here we enumerate the headers to collect the origin header if any
        String origin = request.getHeader(ORIGIN_KEY);
        // determine case by origin header; if lowercase, response headers will be lowercase
        if (origin != null) {
            corsConfig.setUseLowerCaseHeaders(true);
        } else {
            origin = request.getHeader("Origin");
        }
        if (isDebug) {
            log.debug("Origin: {}", origin);
        }
        // local values to prevent extra calls
        String allowedOrigins = corsConfig.getAllowedOrigins();
        String allowedMethods = corsConfig.getAllowedMethods();
        String allowedHeaders = corsConfig.getAllowedHeaders();
        boolean exposeAllHeaders = corsConfig.isExposeAllHeaders();
        String maxAge = corsConfig.getMaxAge();
        boolean allowCredentials = corsConfig.isAllowCredentials();
        boolean useLowerCaseHeaders = corsConfig.isUseLowerCaseHeaders();
        // It they have configured to allow credentials (which makes "*" invalid),
        // then configure the allowed origin based on the origin header and the configuration.
        if (allowCredentials && origin != null) {
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
