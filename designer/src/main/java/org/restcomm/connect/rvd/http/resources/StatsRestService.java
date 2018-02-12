package org.restcomm.connect.rvd.http.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Level;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.concurrency.ProjectRegistry;
import org.restcomm.connect.rvd.concurrency.ResidentProjectInfo;
import org.restcomm.connect.rvd.exceptions.AuthorizationException;
import org.restcomm.connect.rvd.exceptions.ProjectDoesNotExist;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.project.StateHeader;
import org.restcomm.connect.rvd.model.stats.AppStatsDto;
import org.restcomm.connect.rvd.stats.AggregateStats;
import org.restcomm.connect.rvd.storage.FsProjectStorage;
import org.restcomm.connect.rvd.storage.OldWorkspaceStorage;
import org.restcomm.connect.rvd.storage.ProjectDao;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import javax.annotation.PostConstruct;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */

@Path("stats")
public class StatsRestService extends SecuredRestService {

    RvdConfiguration config;
    OldWorkspaceStorage workspace; // CAUTION: does not support operation that need marshaller

    @PostConstruct
    public void init() {
        super.init();
        config = applicationContext.getConfiguration();
        workspace = new OldWorkspaceStorage(config.getWorkspaceBasePath(), null); //  no need for marshaller for checking project existence
    }

    /**
     * Makes sure a user is logged in and a role is available
     */
    protected void secure() {
        super.secure();
    }

    @GET
    @Path("app/{appId}")
    public Response getApplicationStatsTotal(@PathParam("appId") String appId) throws StorageException, ProjectDoesNotExist {
        checkApplicationAccess(appId);

        ProjectRegistry registry = applicationContext.getProjectRegistry();
        ResidentProjectInfo projectInfo = registry.getResidentProjectInfo(appId);

        AppStatsDto dto = new AppStatsDto();
        dto.setInstanceId(config.getRvdInstanceId());
        dto.setRcmlRequestsTotal(projectInfo.stats.rcmlRequestsTotal.get());
        dto.setStartTime(projectInfo.stats.startTime.get());
        dto.setEsCallsPending(projectInfo.stats.esCallsPending.get());
        dto.setEsCallsServerError(projectInfo.stats.esCallsServerError.get());
        dto.setEsCallsTimeout(projectInfo.stats.esCallsTimeout.get());
        dto.setEsCallsSuccess(projectInfo.stats.esCallsSuccess.get());
        dto.setEsCallsTotal(projectInfo.stats.esCallsTotal.get());

        Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").create();
        String data = gson.toJson(dto);
        return Response.ok(data, MediaType.APPLICATION_JSON).build();
    }


    @DELETE
    @Path("app/{appId}")
    public Response resetApplicationStats(@PathParam("appId") String appId) throws StorageException, ProjectDoesNotExist {
        checkApplicationAccess(appId);

        ResidentProjectInfo projectInfo = applicationContext.getProjectRegistry().getResidentProjectInfo(appId);
        projectInfo.setStats(new AggregateStats());
        RvdLoggers.local.log(Level.INFO, "Application stats were reset" ); // TODO include application sid in logging statement
        return Response.noContent().build();
    }

    @GET
    @Path("global")
    public Response getGlobalStatsTotal() {
        checkGlobalAccess();

        AggregateStats stats = applicationContext.getGlobalStats();

        AppStatsDto dto = new AppStatsDto();
        dto.setInstanceId(config.getRvdInstanceId());
        dto.setRcmlRequestsTotal(stats.rcmlRequestsTotal.get());
        dto.setStartTime(stats.startTime.get());
        dto.setEsCallsPending(stats.esCallsPending.get());
        dto.setEsCallsServerError(stats.esCallsServerError.get());
        dto.setEsCallsTimeout(stats.esCallsTimeout.get());
        dto.setEsCallsSuccess(stats.esCallsSuccess.get());
        dto.setEsCallsTotal(stats.esCallsTotal.get());

        Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").create();
        String data = gson.toJson(dto);
        return Response.ok(data, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("global")
    public Response resetGlobalStats() {
        checkGlobalAccess();

        applicationContext.setGlobalStats(new AggregateStats()); // atomic operation...right ?
        RvdLoggers.local.log(Level.INFO, "Global stats were reset" );
        return Response.noContent().build();
    }

    /**
     * Check for all prerequisites before allowing access to the application stats api.
     *
     * - the application should exist
     * - accounts with 'Administrator' role or the owner of the application are allowed
     *
     * Note that checking for 'Administrator' is correct in strict security terms as we should check for
     * super-administrator credentials @see #checkGlobalAccess()
     *
     * @param appId
     * @throws ProjectDoesNotExist
     * @throws StorageException
     */
    void checkApplicationAccess(String appId) throws ProjectDoesNotExist, StorageException {
        secure();
        // make sure the project exists
        OldWorkspaceStorage workspace = new OldWorkspaceStorage(config.getWorkspaceBasePath(), null); //  no need for marshaller for checking project existence
        ProjectDao projectDao = buildProjectDao(workspace);
        if (projectDao.projectExists(appId))
            throw new ProjectDoesNotExist(appId);
        // get project owner
        StateHeader projectHeader = FsProjectStorage.loadStateHeader(appId, workspace);
        String owner = projectHeader.getOwner();
        // get role and email from incoming request
        String clientEmail = getUserIdentityContext().getAccountInfo().getEmail_address();
        String clientRole = getUserIdentityContext().getAccountInfo().getRole();
        // access control - user should be either Administrator or owner of the project
        if (! (RvdConfiguration.ADMINISTRATOR_ROLE.equals(clientRole) || (clientEmail != null && clientEmail.equals(owner)) ))
            throw new AuthorizationException();

    }

    /**
     * Makes sure that everything is in place in order to access the global stats api
     *
     * Note, it checks for 'Administrator' role in the operating account. In strict security terms it should
     * only allow super-administrators. Since 'super-administrator' account means 'not having a parent' and
     * currently restcomm api does not return parent account information, there is no way to really tell if
     * the client is a super-administrator. Thus, we fallback to the check for common 'Admininstrator' role.
     *
     * @throws AuthorizationException in case the user is not administrator
     */
    void checkGlobalAccess() {
        secure();
        // get role from incoming request
        String clientRole = getUserIdentityContext().getAccountInfo().getRole();
        // access control - user should be Administrator
        // TODO - checking for 'Administrator' role is not the right way if a user is super-administrator. This has to be fixed
        if (! "Administrator".equals(clientRole) )
            throw new AuthorizationException();
    }


}
