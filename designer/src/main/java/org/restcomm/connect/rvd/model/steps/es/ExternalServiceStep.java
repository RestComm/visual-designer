package org.restcomm.connect.rvd.model.steps.es;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.concurrency.ResidentProjectInfo;
import org.restcomm.connect.rvd.exceptions.ESRequestException;
import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.interpreter.exceptions.BadExternalServiceResponse;
import org.restcomm.connect.rvd.interpreter.exceptions.ErrorParsingExternalServiceUrl;
import org.restcomm.connect.rvd.interpreter.exceptions.ESProcessFailed;
import org.restcomm.connect.rvd.interpreter.exceptions.RemoteServiceError;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.logging.system.LoggingHelper;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.project.Step;
import org.restcomm.connect.rvd.model.client.UrlParam;
import org.restcomm.connect.rvd.model.rcml.RcmlStep;
import org.restcomm.connect.rvd.stats.AggregateStats;
import org.restcomm.connect.rvd.stats.StatsHelper;
import org.restcomm.connect.rvd.utils.RvdUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;


public class ExternalServiceStep extends Step {
    public static final String CONTENT_TYPE_WWWFORM = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_JSON = "application/json";

    private String url; // supports RVD variable expansion when executing the HTTP request
    private String method;
    private String username;
    private String password;
    private List<UrlParam> urlParams;
    private String contentType;
    private String requestBody;
    private Boolean populatePostBodyFromParams;
    private List<Assignment> assignments;
    private String next;
    private String nextVariable;
    private Boolean doRouting;
    private String nextType;
    private ValueExtractor nextValueExtractor;
    private List<RouteMapping> routeMappings;
    //private String defaultNext;
    private String exceptionNext;
    private Integer timeout; // timeout in milliseconds
    private String onTimeout;


    public ValueExtractor getNextValueExtractor() {
        return nextValueExtractor;
    }

    public void setNextValueExtractor(ValueExtractor nextValueExtractor) {
        this.nextValueExtractor = nextValueExtractor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public List<RouteMapping> getRouteMappings() {
        return routeMappings;
    }

    public void setRouteMappings(List<RouteMapping> routeMappings) {
        this.routeMappings = routeMappings;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public Boolean getDoRouting() {
        return doRouting;
    }

    public void setDoRouting(Boolean doRouting) {
        this.doRouting = doRouting;
    }

    public String getNextType() {
        return nextType;
    }

    public void setNextType(String nextType) {
        this.nextType = nextType;
    }

    public String getNextVariable() {
        return nextVariable;
    }

    public void setNextVariable(String nextVariable) {
        this.nextVariable = nextVariable;
    }
    public List<UrlParam> getUrlParams() {
        return this.urlParams;
    }

    public String getMethod() {
        return method;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    public String getExceptionNext() {
        return exceptionNext;
    }

    public void setExceptionNext(String exceptionNext) {
        this.exceptionNext = exceptionNext;
    }

    public String getOnTimeout() {
        return onTimeout;
    }

    public Integer getTimeout() {
        return timeout;
    }

    @Override
    public RcmlStep render(Interpreter interpreter) throws InterpreterException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String process(Interpreter interpreter, HttpServletRequest httpRequest ) throws InterpreterException {
        // cache this for easier access
        LoggingContext logging = interpreter.getLoggingContext();
        Integer requestTimeout = null;
        ResidentProjectInfo projectInfo = interpreter.getApplicationContext().getProjectRegistry().getResidentProjectInfo(interpreter.getAppName());
        AggregateStats globalStats = interpreter.getApplicationContext().getGlobalStats();
        // count ES request
        StatsHelper.countEsCallTotal(projectInfo.stats);
        StatsHelper.countEsCallTotal(globalStats);

        String next = null;
        try {

            // *** Build the request uri ***

            URI url;
            try {
                URIBuilder uri_builder = new URIBuilder(interpreter.populateVariables(getUrl()) ); // supports RVD variable expansion

                // if this is a relative url fill in missing fields from the request
                if (uri_builder.getHost() == null ) {
                    uri_builder.setScheme(httpRequest.getScheme());
                    uri_builder.setHost(httpRequest.getServerName());
                    uri_builder.setPort(httpRequest.getServerPort());
                    if (  ! uri_builder.getPath().startsWith("/") )
                        uri_builder.setPath("/" + uri_builder.getPath());
                }

                // for GET requests add  url parameters
                if ( getMethod() == null || "GET".equals(getMethod()) || "DELETE".equals(getMethod()) )
                    for ( UrlParam urlParam : getUrlParams() )
                        uri_builder.addParameter(urlParam.getName(), interpreter.populateVariables(urlParam.getValue()) );

                url = uri_builder.build();
            } catch (URISyntaxException e) {
                throw new ErrorParsingExternalServiceUrl( "URL: " + getUrl(), e);
            }


            // *** Make the request and get a status code and a response. Build a JsonElement from the response  ***

            // Set the request timeout. Try with ES element 'timeout' property and if not set fallback to global configuration setting.
            Integer configTimeout = interpreter.getConfiguration().getExternalServiceTimeout();
            if (getTimeout() != null)
                requestTimeout = getTimeout();
            else
                requestTimeout = interpreter.getConfiguration().getExternalServiceTimeout();
            // if the effective timeout is greater than the one specified in configuration, truncate it to that value.
            if (requestTimeout > configTimeout)
                requestTimeout = configTimeout;
            HttpContext httpContext = new BasicHttpContext();
            httpContext.setAttribute(HttpClientContext.REQUEST_CONFIG, RequestConfig.custom().
                    setConnectTimeout(requestTimeout).
                    setSocketTimeout(requestTimeout).
                    setConnectionRequestTimeout(requestTimeout).build());
            CloseableHttpClient client = (CloseableHttpClient) interpreter.getApplicationContext().getExternaltHttpClient();
            CloseableHttpResponse response;
            int statusCode;
            JsonElement response_element = null;

            if (RvdLoggers.local.isDebugEnabled())
                RvdLoggers.local.log(Level.DEBUG, LoggingHelper.buildMessage(getClass(),"process",logging.getPrefix(), "requesting from url: " + url));
            if ( interpreter.getProjectSettings().getLogging() )
                interpreter.getProjectLogger().log("Requesting from url: " + url).tag("app",interpreter.getAppName()).tag("ES").tag("REQUEST").done();

            if ( "POST".equals(getMethod()) || "PUT".equals(getMethod()) ) {

                // Setup request object
                HttpEntityEnclosingRequestBase request;
                if ( "POST".equals(getMethod()) )
                    request = new HttpPost(url);
                else
                    request = new HttpPut(url);

                String body = interpreter.populateVariables(requestBody);

                if ( RvdUtils.isEmpty(getContentType()) || getContentType().equals(CONTENT_TYPE_WWWFORM) ) {
                    // use www-form url-encoded content type
                    if ( !RvdUtils.isEmpty(this.populatePostBodyFromParams) && this.populatePostBodyFromParams ) {
                        List <NameValuePair> values = new ArrayList <NameValuePair>();
                        for ( UrlParam urlParam : getUrlParams() )
                            values.add(new BasicNameValuePair(urlParam.getName(), interpreter.populateVariables(urlParam.getValue()) ));
                        request.setEntity(new UrlEncodedFormEntity(values));
                    } else {
                        request.addHeader("Content-Type","application/x-www-form-urlencoded");
                        if (body == null)
                            request.setEntity(null);
                        else
                            request.setEntity( new StringEntity(body,"UTF-8") );
                    }
                } else
                if ( getContentType().equals(CONTENT_TYPE_JSON) ) {
                    // send the request as JSON
                    request.addHeader("Content-Type","application/json");
                    StringEntity stringBody = new StringEntity(body,"UTF-8");
                    request.setEntity(stringBody);
                } else {
                    // unknown content type found. Use this content type and hope for the best
                    RvdLoggers.local.log(Level.WARN, LoggingHelper.buildMessage(getClass(),"process","{0} unknown content type found when POSTing to {1}: {2}", new Object[] {logging.getPrefix(), url, getContentType()}));
                    request.addHeader("Content-Type", getContentType());
                    StringEntity stringBody = new StringEntity(body,"UTF-8");
                    request.setEntity(stringBody);
                }

                // Add authentication headers if present
                if ( !RvdUtils.isEmpty(getUsername()) )
                    request.addHeader("Authorization", "Basic " + RvdUtils.buildHttpAuthorizationToken(getUsername(), getPassword()));
                // inject the Call ID as an HTTP header to help tracking calls
                if (!RvdUtils.isEmpty(interpreter.getVariables().get(RvdConfiguration.CORE_VARIABLE_PREFIX + "CallSid")))
                    request.addHeader("X-RestComm-CallSid", interpreter.getVariables().get(RvdConfiguration.CORE_VARIABLE_PREFIX + "CallSid"));

                String appName = interpreter.getAppName(); // TODO remove me!!
                try {
                    // mark ES call as pending
                    StatsHelper.countEsCallPending(projectInfo.stats,1);
                    StatsHelper.countEsCallPending(globalStats, 1);
                    response = client.execute(request, httpContext);
                } finally {
                    // 'mark' as not pending when thread is unblocked
                    StatsHelper.countEsCallPending(projectInfo.stats, -1); // decrease pending ES counter
                    StatsHelper.countEsCallPending(globalStats, -1);
                }
            } else
            if ( getMethod() == null || getMethod().equals("GET") || getMethod().equals("DELETE") ) {
                HttpRequestBase request;
                if ( getMethod() == null || getMethod().equals("GET") )
                    request = new HttpGet( url );
                else
                    request = new HttpDelete( url );

                if ( !RvdUtils.isEmpty(getUsername()) )
                    request.addHeader("Authorization", "Basic " + RvdUtils.buildHttpAuthorizationToken(getUsername(), getPassword()));
                if (!RvdUtils.isEmpty(interpreter.getVariables().get(RvdConfiguration.CORE_VARIABLE_PREFIX + "CallSid")))
                    request.addHeader("X-RestComm-CallSid", interpreter.getVariables().get(RvdConfiguration.CORE_VARIABLE_PREFIX + "CallSid"));


                try {
                    // mark ES call as pending
                    StatsHelper.countEsCallPending(projectInfo.stats, 1);
                    StatsHelper.countEsCallPending(globalStats, 1);
                    response = client.execute(request);
                } finally {
                    StatsHelper.countEsCallPending(projectInfo.stats, -1); // decrease pending ES counter
                    StatsHelper.countEsCallPending(globalStats, -1);
                }
            } else
                throw new InterpreterException("Unknonwn HTTP method specified: " + getMethod() );

            // got response
            try {

                statusCode = response.getStatusLine().getStatusCode();

                // In  case of error in the service no need to proceed. Just continue the "onException" module if set
                if (statusCode >= 400 && statusCode < 600) {
                    // counts HTTP errors returned
                    StatsHelper.countEsCallServerError(projectInfo.stats);
                    StatsHelper.countEsCallServerError(globalStats);
                    // logging
                    if (RvdLoggers.local.isEnabledFor(Level.INFO))
                        RvdLoggers.local.log(Level.INFO, LoggingHelper.buildMessage(getClass(),"process", logging.getPrefix(), " remove service failed with: " + response.getStatusLine()));
                    // invoke exception handler module if set
                    if (!RvdUtils.isEmpty(getExceptionNext()))
                        return getExceptionNext();
                    else
                        throw new RemoteServiceError("Service " + url + " failed with: " + response.getStatusLine() + ". Throwing an error since no 'On Remote Exception' has been defined.");
                }
                // Parse the response if (a) there are assignments or (b) there is dynamic or mapped routing
                if (getAssignments() != null && getAssignments().size() > 0
                        || getDoRouting() && ("responseBased".equals(getNextType()) || "mapped".equals(getNextType()))) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        JsonParser parser = new JsonParser();
                        String entity_string = EntityUtils.toString(entity);
                        //logger.info("ES: Received " + entity_string.length() + " bytes");
                        //logger.debug("ES Response: " + entity_string);
                        if (interpreter.getProjectSettings().getLogging())
                            interpreter.getProjectLogger().log(entity_string).tag("app", interpreter.getAppName()).tag("ES").tag("RESPONSE").done();
                        response_element = parser.parse(entity_string);
                    }
                } else {
                    if (RvdLoggers.local.isTraceEnabled())
                        RvdLoggers.local.log(Level.TRACE, LoggingHelper.buildMessage(getClass(),"process", logging.getPrefix(), " no parsing will be done to the response"));
                }
            } catch (JsonSyntaxException e) {
                throw new BadExternalServiceResponse("External Service request received a malformed JSON response" );

            } finally {
                if (response != null) {
                    response.close();
                    client = null;
                }
            }

            // *** Determine what to do next. Find the next module name or whether to continue in the current module ***

            if (getDoRouting()) {
                if ("fixed".equals(getNextType()))
                    next = getNext();
                else if ("responseBased".equals(getNextType()) || "mapped".equals(getNextType())) {
                    String nextValue = interpreter.evaluateExtractorExpression(getNextValueExtractor(), response_element);

                    if ("responseBased".equals(getNextType())) {
                        next = interpreter.getNodeNameByLabel(nextValue);
                    } else if ("mapped".equals(getNextType())) {
                        if (getRouteMappings() != null) {
                            for (RouteMapping mapping : getRouteMappings()) {
                                if (nextValue != null && nextValue.equals(mapping.getValue())) {
                                    next = mapping.getNext();
                                    break;
                                }
                            }
                        }
                    }
                }
                // if no next route has been found throw an error
                if ("fixed".equals(getNextType()) && RvdUtils.isEmpty(next)) {
                    throw new InterpreterException("No valid module could be found for ES routing"); // use a general exception for now.
                }
                if (RvdLoggers.local.isDebugEnabled())
                    RvdLoggers.local.log(Level.DEBUG, LoggingHelper.buildMessage(getClass(),"process", logging.getPrefix(), " routing enabled. Chosen target: " + next));
            }

            // *** Perform the assignments ***

            try {
                if ( getDoRouting() && ("responseBased".equals(getNextType()) || "mapped".equals(getNextType())) ) {
                    for ( Assignment assignment : getAssignments() ) {
                        if (RvdLoggers.local.isEnabledFor(Level.ALL))
                            RvdLoggers.local.log(Level.ALL, LoggingHelper.buildMessage(getClass(),"process","{0} working on variable {1}:{2}", new Object[] {logging.getPrefix(), assignment.getModuleNameScope(), assignment.getDestVariable()}));
                        if ( assignment.getModuleNameScope() == null || assignment.getModuleNameScope().equals(next) ) {
                            String value = null;
                            try {
                                value = interpreter.evaluateExtractorExpression(assignment.getValueExtractor(), response_element);
                            } catch ( BadExternalServiceResponse e ) {
                                throw new ESProcessFailed("Could not parse variable '"  + assignment.getDestVariable() + "'. Variable not found in response" + " - " + (e.getMessage() != null ? " - " + e.getMessage() : ""));
                            }

                            if ( "application".equals(assignment.getScope()) )
                                interpreter.putStickyVariable(assignment.getDestVariable(), value);
                            if ( "module".equals(assignment.getScope()) )
                                interpreter.putModuleVariable(assignment.getDestVariable(), value);

                            //interpreter.putVariable(assignment.getDestVariable(), value );
                        } // else skip assignment
                    }
                }  else {
                    for ( Assignment assignment : getAssignments() ) {
                        if (RvdLoggers.local.isEnabledFor(Level.ALL))
                            RvdLoggers.local.log(Level.ALL, LoggingHelper.buildMessage(getClass(),"process","{0} working on variable {1}", new Object[] {logging.getPrefix(), assignment.getDestVariable()}));
                        String value = null;
                        try {
                            value = interpreter.evaluateExtractorExpression(assignment.getValueExtractor(), response_element);
                        } catch ( BadExternalServiceResponse e ) {
                            throw new ESProcessFailed("Could not parse variable '"  + assignment.getDestVariable() + "'. Variable not found in response" + (e.getMessage() != null ? " - " + e.getMessage() : ""));
                        }

                        if ( "application".equals(assignment.getScope()) )
                            interpreter.putStickyVariable(assignment.getDestVariable(), value);
                        if ( "module".equals(assignment.getScope()) )
                            interpreter.putModuleVariable(assignment.getDestVariable(), value);

                        //interpreter.putVariable(assignment.getDestVariable(), value );
                    }
                }
                if (RvdLoggers.local.isTraceEnabled())
                    RvdLoggers.local.log(Level.TRACE, LoggingHelper.buildMessage(getClass(),"process","{0} variables after processing ExternalService step: {1}", new Object[] {logging.getPrefix(), interpreter.getVariables().toString()}));
            } catch (JsonSyntaxException e) {
                throw new BadExternalServiceResponse("External Service request received a malformed JSON response" );
            }
            // at this point we know we've had a successfull request with a valid and properly parsed response
            // count the request as succesfull
            StatsHelper.countEsCallSuccess(projectInfo.stats);
            StatsHelper.countEsCallSuccess(globalStats);

        } catch (IOException e) {
            // it this is a timeout error log and invoke onTimeout handler
            if (e instanceof SocketTimeoutException) {
                // count this ES request as timed-out
                StatsHelper.countEsCallTimeout(projectInfo.stats);
                StatsHelper.countEsCallTimeout(globalStats);
                // logging
                String message = LoggingHelper.buildMessage(getClass(), "process", "[notify] {0} request to {1} timed out. Effective timeout was {2} ms.", new Object[]{logging.getPrefix(), getUrl(), requestTimeout});
                RvdLoggers.local.log(Level.WARN, message);
                if ( interpreter.getProjectSettings().getLogging() )
                    interpreter.getProjectLogger().log("Request timed out. Timeout set to " + requestTimeout).tag("app",interpreter.getAppName()).tag("ES").done();
                // invoce onTimeout handler
                if ( !RvdUtils.isEmpty(this.onTimeout) )
                    next = this.onTimeout;
            } else
                throw new ESRequestException("Problem while processing ExternalService step " + getName() + (e.getMessage() != null ? (" - " + e.getMessage()) : ""), e);
        }
        return next;
    }

}
