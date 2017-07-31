package org.restcomm.connect.rvd.http.resources;

import com.google.gson.Gson;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.concurrency.ProjectRegistry;
import org.restcomm.connect.rvd.concurrency.ResidentProjectInfo;
import org.restcomm.connect.rvd.model.stats.TotalStatsDto;

import javax.annotation.PostConstruct;
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
    @Path("app/{appId}/totals")
    public Response getApplicationStatsTotal(@PathParam("appId") String appId) {
        ProjectRegistry registry = applicationContext.getProjectRegistry();
        ResidentProjectInfo projectInfo = registry.getResidentProjectInfo(appId);

        TotalStatsDto dto = new TotalStatsDto();
        dto.setRcmlRequestsTotal(projectInfo.stats.rcmlRequestsTotal.get());
        dto.setStartTime(projectInfo.stats.startTime.get());
        //dto.
        Gson gson = new Gson();
        String data = gson.toJson(dto);
        return Response.ok(data, MediaType.APPLICATION_JSON).build();
    }

}
