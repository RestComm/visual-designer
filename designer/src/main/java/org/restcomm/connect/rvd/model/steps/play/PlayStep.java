/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, Telestax Inc and individual contributors
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
 */

package org.restcomm.connect.rvd.model.steps.play;

import java.net.URISyntaxException;
import java.util.logging.Level;

import org.apache.http.client.utils.URIBuilder;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.logging.system.LoggingContext;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.model.client.Step;
import org.restcomm.connect.rvd.model.rcml.RcmlStep;

public class PlayStep extends Step {
    private Integer loop;
    private String playType;
    private Local local;
    private Remote remote;

    public final class Local {
        private String wavLocalFilename;
    }
    public final class Remote {
        private String wavUrl;
    }


    @Override
    public RcmlStep render(Interpreter interpreter) {
        LoggingContext logging = interpreter.getRvdContext().logging;
        RcmlPlayStep playStep = new RcmlPlayStep();
        String url = "";
        if ("local".equals(playType)) {
            String rawurl = interpreter.getContextPath() + "/services/projects/" + interpreter.getAppName() + "/wavs/" + local.wavLocalFilename;
            try {
                URIBuilder uribuilder = new URIBuilder();
                uribuilder.setPath(rawurl);
                url = uribuilder.build().toString();
            } catch (URISyntaxException e) {
                if (RvdLoggers.local.isLoggable(Level.WARNING))
                    RvdLoggers.local.log(Level.WARNING, logging.getPrefix() + "error parsing url for play verb: " + rawurl, e);
                url = rawurl; // best effort
            }
        }
        else {
            url = interpreter.populateVariables(remote.wavUrl);
        }

        if (RvdLoggers.local.isLoggable(Level.FINER))
            RvdLoggers.local.log(Level.FINER, "{0} play url: {1}", new Object[] {logging.getPrefix(),url});

        playStep.setWavurl(url);
        playStep.setLoop(loop);

        return playStep;
    }

}
