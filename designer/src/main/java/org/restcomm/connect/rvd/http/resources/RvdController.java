package org.restcomm.connect.rvd.http.resources;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.restcomm.connect.rvd.ProjectAwareRvdContext;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.exceptions.AccessApiException;
import org.restcomm.connect.rvd.exceptions.ESRequestException;
import org.restcomm.connect.rvd.exceptions.ProjectDoesNotExist;
import org.restcomm.connect.rvd.exceptions.callcontrol.CallControlBadRequestException;
import org.restcomm.connect.rvd.exceptions.callcontrol.CallControlException;
import org.restcomm.connect.rvd.exceptions.callcontrol.CallControlInvalidConfigurationException;
import org.restcomm.connect.rvd.exceptions.callcontrol.UnauthorizedCallControlAccess;
import org.restcomm.connect.rvd.exceptions.ResponseWrapperException;
import org.restcomm.connect.rvd.identity.AccountProvider;
import org.restcomm.connect.rvd.identity.UserIdentityContext;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.interpreter.exceptions.BadExternalServiceResponse;
import org.restcomm.connect.rvd.interpreter.exceptions.ESProcessFailed;
import org.restcomm.connect.rvd.interpreter.exceptions.RemoteServiceError;
import org.restcomm.connect.rvd.logging.system.SystemLoggers;
import org.restcomm.connect.rvd.model.CallControlInfo;
import org.restcomm.connect.rvd.model.ModelMarshaler;
import org.restcomm.connect.rvd.model.ProjectSettings;
import org.restcomm.connect.rvd.model.UserProfile;
import org.restcomm.connect.rvd.model.callcontrol.CallControlAction;
import org.restcomm.connect.rvd.model.callcontrol.CallControlStatus;
import org.restcomm.connect.rvd.model.client.StateHeader;
import org.restcomm.connect.rvd.restcomm.RestcommAccountInfo;
import org.restcomm.connect.rvd.restcomm.RestcommClient;
import org.restcomm.connect.rvd.restcomm.RestcommCallArray;
import org.restcomm.connect.rvd.storage.FsProfileDao;
import org.restcomm.connect.rvd.storage.ProfileDao;
import org.restcomm.connect.rvd.storage.FsProjectStorage;
import org.restcomm.connect.rvd.storage.WorkspaceStorage;
import org.restcomm.connect.rvd.storage.FsCallControlInfoStorage;
import org.restcomm.connect.rvd.storage.exceptions.StorageEntityNotFound;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.storage.exceptions.WavItemDoesNotExist;
import org.restcomm.connect.rvd.utils.RvdUtils;

@Path("apps")
public class RvdController extends SecuredRestService {
    static final Logger logger = Logger.getLogger(RvdController.class.getName());
    String loggerPrefix = ""; // prefix information including app Id and call Id to be prefixed to log messages.
    Pattern appIdPattern = Pattern.compile("^apps\\/([a-zA-Z0-9]+)(\\/|$)");

    private RvdConfiguration rvdSettings;
    private ProjectAwareRvdContext rvdContext;

    private WorkspaceStorage workspaceStorage;
    private ModelMarshaler marshaler;
    @Context
    UriInfo uriInfo;
    String applicationId; // contains a valid applicationId

    @PostConstruct
    public void init() {
        super.init();
        // An application SID is required for all RvdController methods. Throw error if it's not there.
        this.applicationId = extractAppIdFromPath(uriInfo.getPath());
        if (applicationId == null)
            throw new ResponseWrapperException( Response.status(Status.BAD_REQUEST).build() );
        try {
            loggerPrefix = "["+applicationId.substring(0, 16)+"] "; // TODO put call ID information here
            rvdContext = new ProjectAwareRvdContext(applicationId, applicationContext.getProjectRegistry().getProjectSemaphores(applicationId),request, servletContext, applicationContext.getConfiguration(), loggerPrefix);
        } catch (ProjectDoesNotExist projectDoesNotExist) {
            throw new ResponseWrapperException( Response.status(Status.NOT_FOUND).build() );
        }
        rvdSettings = rvdContext.getSettings();
        marshaler = rvdContext.getMarshaler();
        workspaceStorage = rvdContext.getWorkspaceStorage();
    }

    public RvdController() {}

    RvdController(UserIdentityContext context) {
        super(context);
    }

    // handle both GET and POST request in a single place
    private Response runInterpreter(String appname, HttpServletRequest httpRequest,
                                    MultivaluedMap<String, String> requestParams) {
        String rcmlResponse;
        try {
            if (!FsProjectStorage.projectExists(appname, workspaceStorage))
                return Response.status(Status.NOT_FOUND).build();

            String targetParam = requestParams.getFirst("target");
            Interpreter interpreter = new Interpreter(rvdContext, targetParam, appname, httpRequest, requestParams,
                    workspaceStorage,applicationContext);
            rcmlResponse = interpreter.interpret();

            // logging rcml response, if configured
            // make sure logging is enabled before allowing access to sensitive log information
            ProjectSettings projectSettings = rvdContext.getProjectSettings();
            if (projectSettings.getLogging() == true && (projectSettings.getLoggingRCML() != null && projectSettings.getLoggingRCML() == true) ){
                interpreter.getProjectLogger().log( rcmlResponse, false).tag("app", appname).tag("RCML").done();
            }

        } catch (RemoteServiceError | ESProcessFailed | BadExternalServiceResponse |ESRequestException e){
            if (SystemLoggers.controller.isLoggable(Level.WARNING))
                SystemLoggers.controller.log(Level.WARNING, loggerPrefix + e.getMessage());
            if (rvdContext.getProjectSettings().getLogging())
                rvdContext.getProjectLogger().log(e.getMessage()).tag("app", appname).tag("EXCEPTION").done();
            rcmlResponse = Interpreter.rcmlOnException();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (rvdContext.getProjectSettings().getLogging())
                rvdContext.getProjectLogger().log(e.getMessage()).tag("app", appname).tag("EXCEPTION").done();
            rcmlResponse = Interpreter.rcmlOnException();
        }
        if(logger.isDebugEnabled()) {
            logger.debug(rcmlResponse);
        }
        return Response.ok(rcmlResponse, MediaType.APPLICATION_XML).build();
    }

    @GET
    @Path("{appname}/controller")
    @Produces(MediaType.APPLICATION_XML)
    public Response controllerGet( @Context HttpServletRequest httpRequest,
            @Context UriInfo ui) {
        if (SystemLoggers.global.isLoggable(Level.INFO))
            SystemLoggers.global.log(Level.INFO, loggerPrefix + " incoming GET request " + (SystemLoggers.global.isLoggable(Level.FINE) ? (ui.getRequestUri().toString()) : "" ));

        Enumeration<String> headerNames = (Enumeration<String>) httpRequest.getHeaderNames();
        // TODO remove this loop (?)
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
        }
        MultivaluedMap<String, String> requestParams = ui.getQueryParameters();

        return runInterpreter(applicationId, httpRequest, requestParams);
    }

    @POST
    @Path("{appname}/controller")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_XML)
    public Response controllerPost(@Context HttpServletRequest httpRequest, MultivaluedMap<String, String> requestParams, @Context UriInfo ui) {
        if (SystemLoggers.global.isLoggable(Level.INFO))
            SystemLoggers.global.log(Level.INFO, loggerPrefix + " incoming POST request " + (SystemLoggers.global.isLoggable(Level.FINE) ? (ui.getRequestUri().toString() + " form: " + requestParams.toString()):"" ));

        return runInterpreter(applicationId, httpRequest, requestParams);
    }

    @GET
    @Path("{appname}/resources/{filename}")
    public Response getWav(@PathParam("filename") String filename) {
        InputStream wavStream;

        try {
            wavStream = FsProjectStorage.getWav(applicationId, filename, workspaceStorage);
            return Response.ok(wavStream, "audio/x-wav")
                    .header("Content-Disposition", "attachment; filename = " + filename).build();
        } catch (WavItemDoesNotExist e) {
            return Response.status(Status.NOT_FOUND).build(); // ordinary error page is returned since this will be consumed
                                                              // either from restcomm or directly from user
        } catch (StorageException e) {
            // return buildErrorResponse(Status.INTERNAL_SERVER_ERROR, RvdResponse.Status.ERROR, e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build(); // ordinary error page is returned since this will
                                                                          // be consumed either from restcomm or directly
                                                                          // from user
        }
    }

    // Web Trigger -----

    private RestcommCallArray executeAction(String projectName, HttpServletRequest request, String toParam,
                                            String fromParam, String accessToken, UriInfo ui, AccountProvider accountProvider) throws StorageException, CallControlException {
        loggerPrefix = loggerPrefix + "[WT] ";
        if (SystemLoggers.global.isLoggable(Level.INFO))
            SystemLoggers.global.log(Level.INFO, loggerPrefix + "incoming triggering request " + ui.getRequestUri().toString());
        if (rvdContext.getProjectSettings().getLogging())
            rvdContext.getProjectLogger().log("WebTrigger incoming request: " + ui.getRequestUri().toString(),false).tag("app", projectName).tag("WebTrigger").done();

        // load CC/WebTrigger project info
        CallControlInfo info = FsCallControlInfoStorage.loadInfo(projectName, workspaceStorage);
        // find the owner of the project
        StateHeader projectHeader = FsProjectStorage.loadStateHeader(projectName,workspaceStorage);
        String owner = projectHeader.getOwner();
        if (RvdUtils.isEmpty(owner))
            throw new CallControlException("Project '" + projectName + "' has no owner and can't be started using WebTrigger.");

        String effectiveAuthHeader = null;
        String accountSid = null;
        if ( ! RvdUtils.isEmpty(info.accessToken)) {
            // there is a token in WebTrigger form, let's try token authentication first
            if ( ! RvdUtils.isEmpty(accessToken) ) {
                // since there is also a token in the request have to authenticate this way
                if ( !info.accessToken.equals(accessToken) )
                    throw new UnauthorizedCallControlAccess("WebTrigger authorization error");
                // load user profile
                ProfileDao profileDao = new FsProfileDao(workspaceStorage);
                UserProfile profile = profileDao.loadUserProfile(owner);
                // if there is no profile at all or if the credentials are missing from it throw an error
                if (profile == null || (RvdUtils.isEmpty(profile.getUsername()) || RvdUtils.isEmpty(profile.getToken())) )
                    throw new UnauthorizedCallControlAccess("Profile missing creds or no profile at all for user '" + owner + "'. Web trigger cannot be used for project belonging to this user.");
                effectiveAuthHeader = RvdUtils.isEmpty(profile.getUsername()) ? null : ("Basic "  + RvdUtils.buildHttpAuthorizationToken(profile.getUsername(), profile.getToken()));
                RestcommAccountInfo accountInfo = accountProvider.getActiveAccount(profile.getUsername(), effectiveAuthHeader).get();
                if (accountInfo == null)
                    throw new UnauthorizedCallControlAccess("WebTrigger authorization error");
                accountSid = accountInfo.getSid();
            }
        }
        if (effectiveAuthHeader == null) {
            // looks like token authentication didn't work. Let's try to use credentials from the request
            if (getUserIdentityContext().getAccountInfo() != null) {
                effectiveAuthHeader = getUserIdentityContext().getEffectiveAuthorizationHeader();
                accountSid = getUserIdentityContext().getAccountInfo().getSid();
            }
        }
        // at this point we should have an authorization header in place
        if ( effectiveAuthHeader == null)
            throw new UnauthorizedCallControlAccess("WebTrigger authorization error");

        // guess restcomm location
        URI restcommBaseUri = applicationContext.getConfiguration().getRestcommBaseUri();
        // initialize a restcomm client object using various information sources
        RestcommClient restcommClient;
        try {
            restcommClient = new RestcommClient(restcommBaseUri, effectiveAuthHeader,applicationContext.getHttpClientBuilder());
        } catch (RestcommClient.RestcommClientInitializationException e) {
            throw new CallControlException("WebTrigger",e);
        }
        if (SystemLoggers.controller.isLoggable(Level.FINE))
            SystemLoggers.controller.log(Level.FINE, loggerPrefix + "reaching restcomm at '" + restcommBaseUri + "'" );

        String rcmlUrl = info.lanes.get(0).startPoint.rcmlUrl;
        // use the existing application for RCML if none has been given
        if (RvdUtils.isEmpty(rcmlUrl)) {
            URIBuilder uriBuilder = new URIBuilder(restcommBaseUri);
            uriBuilder.setPath("/restcomm-rvd/services/apps/" + projectName + "/controller");
            try {
                rcmlUrl = uriBuilder.build().toString();
            } catch (URISyntaxException e) {
                throw new CallControlException("URI parsing error while generating the rcml url", e);
            }
        }

        // Add user supplied params to rcmlUrl
        try {
            URIBuilder uriBuilder = new URIBuilder(rcmlUrl);
            MultivaluedMap<String, String> requestParams = ui.getQueryParameters();
            for (String paramName : requestParams.keySet()) {
                if ("token".equals(paramName) || "from".equals(paramName) || "to".equals(paramName))
                    continue; // skip parameters that are used by WebTrigger itself i.e. to/from/token
                // ignore builtin parameters that will be supplied by restcomm when it reaches for the controller
                if (!rvdSettings.getRestcommParameterNames().contains(paramName)) {
                    // the rest are consider user-supplied params and need to be propagated to the url
                    if (RvdUtils.isEmpty(info.userParamScope) || "mod".equals(info.userParamScope))
                        // create module scoped param if userParamScope is 'mod' or empty
                        uriBuilder.addParameter(Interpreter.nameModuleRequestParam(paramName), requestParams.getFirst(paramName));
                    else
                    if ("app".equals(info.userParamScope))
                        uriBuilder.addParameter(Interpreter.nameStickyRequestParam(paramName), requestParams.getFirst(paramName));
                    else
                        throw new UnsupportedOperationException("WebTrigger userParamScope not supported: " + info.userParamScope);
                }
            }
            rcmlUrl = uriBuilder.build().toString();
        } catch (URISyntaxException e) {
            throw new CallControlException("Error copying user supplied parameters to rcml url", e);
        }

        if (SystemLoggers.controller.isLoggable(Level.FINE))
            SystemLoggers.controller.log(Level.FINE, loggerPrefix + "rcmlUrl: " + rcmlUrl);

        // to: Use value from url. If specified in WebTrigger form, override it.
        String to = toParam;
        String toOverride = info.lanes.get(0).startPoint.to;
        if (!RvdUtils.isEmpty(toOverride))
            to = toOverride;

        // from: Use value i the url. If specified in WebTrigger form override it. If none is defined use part of application name.
        String from = fromParam;
        String fromOverride = info.lanes.get(0).startPoint.from;
        if (!RvdUtils.isEmpty(fromOverride))
            from = fromOverride;
        // If none is defined use part of application name.
        if (RvdUtils.isEmpty(from)) {
            if (!RvdUtils.isEmpty(projectName))
                from = projectName.substring(0, projectName.length() < 10 ? projectName.length() : 10);
        }

        if (RvdUtils.isEmpty(rcmlUrl))
            throw new CallControlInvalidConfigurationException("Could not determine application RCML url.");
        if (RvdUtils.isEmpty(from) || RvdUtils.isEmpty(to))
            throw new CallControlBadRequestException(
                    "Either <i>from</i> or <i>to</i> value is missing. Make sure they are both passed as query parameters or are defined in the Web Trigger configuration.")
                    .setStatusCode(400);

        try {

            // Find the account sid for the apiUsername is not available
            if (RvdUtils.isEmpty(accountSid)) {
                RestcommAccountInfo accountResponse = restcommClient.get("/restcomm/2012-04-24/Accounts.json/" + getLoggedUsername()).done(
                        marshaler.getGson(), RestcommAccountInfo.class);
                accountSid = accountResponse.getSid();
            }
            // Create the call
            RestcommCallArray response = restcommClient.post("/restcomm/2012-04-24/Accounts/" + accountSid + "/Calls.json")
                    .addParam("From", from).addParam("To", to).addParam("Url", rcmlUrl)
                    .done(marshaler.getGson(), RestcommCallArray.class);

            if (SystemLoggers.controller.isLoggable(Level.FINE))
                SystemLoggers.controller.log(Level.FINE, loggerPrefix + "joined '" + to + "' with " + rcmlUrl);
            return response;
        } catch (AccessApiException e) {
            throw new CallControlException(e.getMessage(), e).setStatusCode(e.getStatusCode());
        }
    }

    @GET
    @Path("{appname}/start{extension: (.html)?}")
    @Produces(MediaType.TEXT_HTML)
    public Response executeActionHtml(@Context HttpServletRequest request,
            @QueryParam("to") String toParam, @QueryParam("from") String fromParam, @QueryParam("token") String accessToken,
            @Context UriInfo ui) {
        String selectedMediaType = MediaType.TEXT_HTML;
        try {
            AccountProvider accountProvider = applicationContext.getAccountProvider();
            RestcommCallArray calls = executeAction(applicationId, request, toParam, fromParam, accessToken, ui, accountProvider);
            // build call-sid part of message
            StringBuffer messageBuffer = new StringBuffer("[");
            for (int i=0; i<calls.size(); i++) {
                messageBuffer.append(calls.get(i).getSid());
                if (i < calls.size()-1)
                    messageBuffer.append(",");
            }
            messageBuffer.append("]");
            return buildWebTriggerHtmlResponse("Web Trigger", "Create call", "success",
                    "Created call with SID " + messageBuffer.toString() + " from " + calls.get(0).getFrom() + " to "
                            + calls.get(0).getTo(), 200);
        } catch (UnauthorizedCallControlAccess e) {
            // TODO check that the e.getMessage() contains adequate information
            if (SystemLoggers.controller.isLoggable(Level.INFO))
                SystemLoggers.controller.log(Level.WARNING, loggerPrefix + e.getMessage());
            //logger.warn(e);
            return buildWebTriggerHtmlResponse("Web Trigger", "Create call", "failure", "Authentication error", 401);
        } catch (CallControlException e) {
            // rcomm log
            // TODO check that the e.getMessage() contains adequate information
            if (SystemLoggers.global.isLoggable(Level.WARNING))
                SystemLoggers.global.log(Level.WARNING, loggerPrefix + e.getMessage());
            //logger.warn(e);
            int httpStatus = 500;
            if (e.getStatusCode() != null)
                httpStatus = e.getStatusCode();
            return buildWebTriggerHtmlResponse("Web Trigger", "Create call", "failure", "", httpStatus);
        } catch (StorageEntityNotFound e) {
            SystemLoggers.global.log(Level.SEVERE, loggerPrefix, e);
            //logger.error("", e);
            return Response.status(Status.NOT_FOUND).build(); // for case when the cc file does not exist
        } catch (StorageException e) {
            SystemLoggers.global.log(Level.SEVERE, loggerPrefix, e);
            //logger.error("", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(selectedMediaType).build();
        }
    }

    @GET
    @Path("{appname}/start.json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response executeActionJson(@Context HttpServletRequest request,
            @QueryParam("to") String toParam, @QueryParam("from") String fromParam, @QueryParam("token") String accessToken,
            @Context UriInfo ui) {
        String selectedMediaType = MediaType.APPLICATION_JSON;
        try {
            AccountProvider accountProvider = applicationContext.getAccountProvider();
            RestcommCallArray calls = executeAction(applicationId, request, toParam, fromParam, accessToken, ui, accountProvider);
            return buildWebTriggerJsonResponse(CallControlAction.createCall, CallControlStatus.success, 200, calls);
        } catch (UnauthorizedCallControlAccess e) {
            // TODO check that the e.getMessage() contains adequate information
            if (SystemLoggers.controller.isLoggable(Level.INFO))
                SystemLoggers.controller.log(Level.WARNING, loggerPrefix + e.getMessage());
            return buildWebTriggerJsonResponse(CallControlAction.createCall, CallControlStatus.failure, 401, null);
        } catch (CallControlException e) {
            // TODO check that the e.getMessage() contains adequate information
            if (SystemLoggers.global.isLoggable(Level.WARNING))
                SystemLoggers.global.log(Level.WARNING, loggerPrefix + e.getMessage());
            int httpStatus = 500;
            if (e.getStatusCode() != null)
                httpStatus = e.getStatusCode();
            return buildWebTriggerJsonResponse(CallControlAction.createCall, CallControlStatus.failure, httpStatus, null);
        } catch (StorageEntityNotFound e) {
            SystemLoggers.global.log(Level.SEVERE, loggerPrefix, e);
            return Response.status(Status.NOT_FOUND).build(); // for case when the cc file does not exist
        } catch (StorageException e) {
            SystemLoggers.global.log(Level.SEVERE, loggerPrefix, e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(selectedMediaType).build();
        }
    }

    @GET
    @Path("{appname}/log")
    public Response appLog() {
        secure();
        try {
            // make sure logging is enabled before allowing access to sensitive log information
            ProjectSettings projectSettings = FsProjectStorage.loadProjectSettings(applicationId, workspaceStorage);
            if (projectSettings == null || projectSettings.getLogging() == false)
                return Response.status(Status.NOT_FOUND).build();
            InputStream logStream;
            try {
                // TODO make sure getLogFilePath() returns the right value here
                logStream = new FileInputStream(rvdContext.getProjectLogger().getLogFilePath());
                return Response.ok(logStream, "text/plain").header("Cache-Control", "no-cache, no-store, must-revalidate")
                        .header("Pragma", "no-cache").build();

                // response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
                // response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
                // response.setDateHeader("Expires", 0);
            } catch (FileNotFoundException e) {
                return Response.status(Status.NOT_FOUND).build(); // nothing to return. There is no log file
            }
        } catch (StorageEntityNotFound e) {
            return Response.status(Status.NOT_FOUND).build();
        } catch (StorageException e1) {
            SystemLoggers.global.log(Level.SEVERE, loggerPrefix, e1);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("{appname}/log")
    public Response resetAppLog() {
        secure();
        try {
            // make sure logging is enabled before allowing access to sensitive log information
            ProjectSettings projectSettings = FsProjectStorage.loadProjectSettings(applicationId, workspaceStorage);
            if (projectSettings == null || projectSettings.getLogging() == false)
                return Response.status(Status.NOT_FOUND).build();

            rvdContext.getProjectLogger().reset();
            return Response.ok().build();
        } catch (StorageException e) {
            // !!! return hangup!!!
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Tries to extract the appId part from paths like: 'apps/APd13dc2c651884534b2fd7c7b98cac354/controller...'
     *
     * @param path
     * @return the extracted application sid or null if nothing is matched
     */
    String extractAppIdFromPath(String path) {
        String uri_string = path.toString();
        Matcher matcher = appIdPattern.matcher(uri_string);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}
