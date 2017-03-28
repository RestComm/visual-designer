package org.restcomm.connect.rvd.bootstrap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.restcomm.connect.rvd.ApplicationContext;
import org.restcomm.connect.rvd.ApplicationContextBuilder;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.commons.http.CustomHttpClientBuilder;
import org.restcomm.connect.rvd.concurrency.ProjectRegistry;
import org.restcomm.connect.rvd.identity.AccountProvider;
import org.restcomm.connect.rvd.logging.system.StaticLoggers;
import org.restcomm.connect.rvd.model.ModelMarshaler;
import org.restcomm.connect.rvd.storage.WorkspaceStorage;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;
import org.restcomm.connect.rvd.upgrade.UpgradeService;


public class RvdInitializationServlet extends HttpServlet {

    static final Logger logger = Logger.getLogger(RvdInitializationServlet.class.getName());

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config) ;
        // Create application context and store in ServletContext
        ServletContext servletContext = config.getServletContext();
        // first initialize RVD system logging
        logger.info("Starting RVD system logging...");
        StaticLoggers.init(servletContext.getRealPath("/../../log/rvd/"));

        if(logger.isInfoEnabled()) {
            logger.info("Initializing RVD. Project version: " + RvdConfiguration.getRvdProjectVersion());
        }

        RvdConfiguration rvdConfiguration = new RvdConfiguration(servletContext);
        CustomHttpClientBuilder httpClientBuilder = new CustomHttpClientBuilder(rvdConfiguration);
        AccountProvider accountProvider = new AccountProvider(rvdConfiguration, httpClientBuilder);
        ApplicationContext appContext = new ApplicationContextBuilder()
                .setConfiguration(rvdConfiguration)
                .setHttpClientBuilder(httpClientBuilder)
                .setAccountProvider(accountProvider)
                .setProjectRegistry(new ProjectRegistry()).build();
        servletContext.setAttribute(ApplicationContext.class.getName(), appContext);

        WorkspaceBootstrapper workspaceBootstrapper = new WorkspaceBootstrapper(rvdConfiguration.getWorkspaceBasePath());
        workspaceBootstrapper.run();

        ModelMarshaler marshaler = new ModelMarshaler();
        WorkspaceStorage workspaceStorage = new WorkspaceStorage(rvdConfiguration.getWorkspaceBasePath(), marshaler);
        UpgradeService upgradeService = new UpgradeService(workspaceStorage);
        try {
            upgradeService.upgradeWorkspace();
        } catch (StorageException e) {
            logger.error("Error upgrading workspace at " + rvdConfiguration.getWorkspaceBasePath(), e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        // need to close logger handlers that linger on
        StaticLoggers.destroy();
    }

    public RvdInitializationServlet() {
        // TODO Auto-generated constructor stub
    }

}
