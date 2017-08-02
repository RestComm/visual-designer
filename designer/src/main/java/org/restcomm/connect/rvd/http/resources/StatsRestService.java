package org.restcomm.connect.rvd.http.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Level;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.concurrency.ProjectRegistry;
import org.restcomm.connect.rvd.concurrency.ResidentProjectInfo;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.stats.AppStatsDto;
import org.restcomm.connect.rvd.stats.AggregateStats;

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

    @PostConstruct
    public void init() {
        super.init();
        config = applicationContext.getConfiguration();
    }

    @GET
    @Path("app/{appId}")
    public Response getApplicationStatsTotal(@PathParam("appId") String appId) {
        ProjectRegistry registry = applicationContext.getProjectRegistry();
        ResidentProjectInfo projectInfo = registry.getResidentProjectInfo(appId);

        AppStatsDto dto = new AppStatsDto();
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
    public Response resetApplicationStats(@PathParam("appId") String appId) {
        ResidentProjectInfo projectInfo = applicationContext.getProjectRegistry().getResidentProjectInfo(appId);
        projectInfo.setStats(new AggregateStats());
        RvdLoggers.local.log(Level.INFO, "Application stats were reset" ); // TODO include application sid in logging statement
        return Response.noContent().build();
    }

    @GET
    @Path("global")
    public Response getGlobalStatsTotal() {
        AggregateStats stats = applicationContext.getGlobalStats();

        AppStatsDto dto = new AppStatsDto();
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
        applicationContext.setGlobalStats(new AggregateStats()); // atomic operation...right ?
        RvdLoggers.local.log(Level.INFO, "Global stats were reset" );
        return Response.noContent().build();
    }


}
