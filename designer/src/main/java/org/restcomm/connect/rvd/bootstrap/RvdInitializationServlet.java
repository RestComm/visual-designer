package org.restcomm.connect.rvd.bootstrap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;

import org.restcomm.connect.rvd.ApplicationContext;
import org.restcomm.connect.rvd.ApplicationContextBuilder;
import org.restcomm.connect.rvd.FileRvdConfiguration;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.commons.http.CustomHttpClientBuilder;
import org.restcomm.connect.rvd.concurrency.ProjectRegistry;
import org.restcomm.connect.rvd.configuration.RestcommLocationResolver;
import org.restcomm.connect.rvd.exceptions.BootstrappingException;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.StepMarshaler;
import org.restcomm.connect.rvd.storage.FsWorkspaceDao;
import org.restcomm.connect.rvd.storage.FsWorkspaceStorage;
import org.restcomm.connect.rvd.storage.JsonModelStorage;
import org.restcomm.connect.rvd.storage.WorkspaceDao;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.upgrade.UpgradeService;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.restcomm.connect.rvd.upgrade.WorkspaceMaintainer;


public class RvdInitializationServlet extends HttpServlet {

    static Logger logger = RvdLoggers.global; // Logger.getLogger("visual-designer");

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config) ;
        // Create application context and store in ServletContext
        ServletContext servletContext = config.getServletContext();
        // first initialize RVD local logging
        RvdLoggers.init(servletContext.getRealPath("/../../log/rvd/"));
        logger.info("--- Initializing RVD. Project version: " + RvdConfiguration.RVD_PROJECT_VERSION + " ---");
        RvdConfiguration rvdConfiguration;
        try {
            FileRvdConfiguration fileRvdconfiguration = new FileRvdConfiguration(servletContext);
            logger.info(fileRvdconfiguration.toString());
            rvdConfiguration = fileRvdconfiguration;
        } catch (Exception e) {
            logger.log(Level.ERROR, "Error loading rvd configuration file rvd.xml. RVD operation will be broken.",e);
            throw e;
        }
        CustomHttpClientBuilder httpClientBuilder = new CustomHttpClientBuilder(rvdConfiguration);
        CloseableHttpClient buildHttpClient = httpClientBuilder.buildHttpClient();
        RestcommLocationResolver restcommResolver = new RestcommLocationResolver(rvdConfiguration);
        ApplicationContext appContext = new ApplicationContextBuilder()
                .setConfiguration(rvdConfiguration)
                .setHttpClientBuilder(httpClientBuilder)
                .setDefaultHttpClient(buildHttpClient)
                .setExternalHttpClient(httpClientBuilder.buildExternalHttpClient())
                .setProjectRegistry(new ProjectRegistry())
                .setRestcommResolver(restcommResolver).build();
        servletContext.setAttribute(ApplicationContext.class.getName(), appContext);

        JsonModelStorage storage = new JsonModelStorage(new FsWorkspaceStorage(rvdConfiguration.getWorkspaceBasePath()), new StepMarshaler());
        WorkspaceDao workspaceDao = new FsWorkspaceDao(storage, rvdConfiguration);
        WorkspaceMaintainer workspaceMaintainer = new WorkspaceMaintainer(workspaceDao);

        WorkspaceBootstrapper workspaceBootstrapper = new WorkspaceBootstrapper(rvdConfiguration.getWorkspaceBasePath(), rvdConfiguration.getProjectTemplatesWorkspacePath());
        try {
            workspaceBootstrapper.run();
        } catch (BootstrappingException e) {
            logger.log(Level.ERROR,"Error bootstrapping workspace at " + rvdConfiguration.getWorkspaceBasePath(), e);
        }

        UpgradeService upgradeService = new UpgradeService(storage);
        try {
            upgradeService.upgradeWorkspace();
        } catch (StorageException e) {
            logger.log(Level.ERROR,"Error upgrading workspace at " + rvdConfiguration.getWorkspaceBasePath(), e);
        }
    }

    @Override
    public void destroy() {
        logger.info(" --- shutting down RVD --- ");
        ApplicationContext appCtx = (ApplicationContext) this.getServletContext().getAttribute(ApplicationContext.class.getName());
        HttpClientUtils.closeQuietly(appCtx.getDefaultHttpClient());
        HttpClientUtils.closeQuietly(appCtx.getExternaltHttpClient());
        super.destroy();
    }

    public RvdInitializationServlet() {
        // TODO Auto-generated constructor stub
    }

}
