package org.restcomm.connect.rvd.http.resources;

import com.google.gson.Gson;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.http.PaginatedResults;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.model.StepMarshaler;
import org.restcomm.connect.rvd.model.ProjectTemplate;
import org.restcomm.connect.rvd.storage.FsProjectTemplateDao;
import org.restcomm.connect.rvd.storage.OldWorkspaceStorage;
import org.restcomm.connect.rvd.storage.ProjectTemplateDao;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.utils.ValidationUtils;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */

@Path("templates")
public class TemplatesRestService extends SecuredRestService {

    private LoggingContext logging;
    private RvdConfiguration configuration;

    @PostConstruct
    public void init() {
        super.init();
        logging = new LoggingContext("[designer]");
        logging.appendAccountSid(getUserIdentityContext().getAccountSid());
        configuration = applicationContext.getConfiguration();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{templateId}")
    public Response getSingleTemplate(@PathParam("templateId") String templateId) throws StorageException {
        secure();
        if ( ! ValidationUtils.validateTemplateId(templateId) )
            return Response.status(Response.Status.BAD_REQUEST).build();

        ProjectTemplateDao dao = buildProjectTemplateDao();
        ProjectTemplate template = dao.loadProjectTemplate(templateId);
        if (template == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Gson gson = new Gson();
        return Response.ok(gson.toJson(template), MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTemplateList(@QueryParam("page") Integer pageIndex) throws StorageException {
        secure();
        ProjectTemplateDao dao = buildProjectTemplateDao();
        PaginatedResults<ProjectTemplate> results = new PaginatedResults<ProjectTemplate>();
        dao.loadProjectTemplates(pageIndex, 100, null, results ); // TODO remove hardcoded pageSize
        Gson gson = new Gson();
        return Response.ok(gson.toJson(results)).build();
    }

    /**
     * ProjectTemplateDao factory method
     *
     * All construction details have been encapsulated in a single method to allow for easily delegating to a builder
     * class in the future.
     *
     * @return a ProjectTemplateDao
     */
    private ProjectTemplateDao buildProjectTemplateDao() {
        OldWorkspaceStorage oldWorkspaceStorage = new OldWorkspaceStorage(configuration.getWorkspaceBasePath(), new StepMarshaler());
        FsProjectTemplateDao dao = new FsProjectTemplateDao(oldWorkspaceStorage, configuration);
        return dao;
    }


}
