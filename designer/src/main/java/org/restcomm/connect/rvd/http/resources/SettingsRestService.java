/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2016, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package org.restcomm.connect.rvd.http.resources;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.log4j.Level;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.identity.UserIdentityContext;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.logging.system.LoggingHelper;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.SimpleMarshaller;
import org.restcomm.connect.rvd.model.UserProfile;
import org.restcomm.connect.rvd.model.client.SettingsModel;
import org.restcomm.connect.rvd.storage.FsProfileDao;
import org.restcomm.connect.rvd.storage.FsWorkspaceStorage;
import org.restcomm.connect.rvd.storage.JsonModelStorage;
import org.restcomm.connect.rvd.storage.ProfileDao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * HTTP endpoint for storage/retrieval of storage settings.
 *
 * @author Orestis Tsakiridis
 */
// TODO rename this to 'profile' as well as method names
@Path("settings")
public class SettingsRestService extends SecuredRestService {

    RvdConfiguration settings;
    SimpleMarshaller marshaler;
    JsonModelStorage storage;
    LoggingContext logging;

    @PostConstruct
    public void init() {
        super.init();
        logging = new LoggingContext("[designer]");
        logging.appendAccountSid(getUserIdentityContext().getAccountSid());
        settings = applicationContext.getConfiguration();
        marshaler = new SimpleMarshaller();
        storage = new JsonModelStorage(new FsWorkspaceStorage(settings.getWorkspaceBasePath()), marshaler);
    }

    public SettingsRestService() {
    }

    SettingsRestService(UserIdentityContext context) {
        super(context);
    }

    @POST
    public Response setProfile(@Context HttpServletRequest request) {
        secure();
        try {
            // Create a settings model from the request
            String data;
            data = IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
            SettingsModel settingsForm = marshaler.toModel(data, SettingsModel.class);
            // update user profile
            ProfileDao profileDao = new FsProfileDao(storage);
            String loggedUsername = getLoggedUsername();
            UserProfile profile = profileDao.loadUserProfile(loggedUsername);
            if (profile == null)
                profile = new UserProfile();
            profile.setUsername(settingsForm.getApiServerUsername());
            profile.setToken(settingsForm.getApiServerPass());
            profileDao.saveUserProfile(loggedUsername, profile);
            if (RvdLoggers.local.isDebugEnabled())
                RvdLoggers.local.log(Level.DEBUG, LoggingHelper.buildMessage(getClass(),"setProfile",logging.getPrefix(),"user profile updated"));
            return Response.ok().build();
        } catch (IOException | JsonSyntaxException e) {
            RvdLoggers.local.log(Level.ERROR, LoggingHelper.buildMessage(getClass(),"setProfile", logging.getPrefix(), e.getMessage()), e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProfile() {
        secure();
        // load user profile
        ProfileDao profileDao = new FsProfileDao(storage);
        String loggedUsername = getLoggedUsername();
        UserProfile profile = profileDao.loadUserProfile(loggedUsername);

        SettingsModel settingsForm = new SettingsModel();
        if (profile != null) {
            settingsForm.setApiServerUsername(profile.getUsername());
            settingsForm.setApiServerPass(profile.getToken());
        }

        // build a response
        Gson gson = new Gson();
        String data = gson.toJson(settingsForm);
        return Response.ok(data, MediaType.APPLICATION_JSON).build();
    }

}
