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
 *
 */

package org.restcomm.connect.rvd.helpers;

import org.restcomm.connect.rvd.model.ProjectParameters;

import java.util.List;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class ProjectParametersHelper {

    public ProjectParametersHelper() {
    }

    /**
     * Populates an existing ProjectParameters object with new values
     *
     * The 'name' and 'description' fields are not touched.
     * Only existing parameters are updated. No new are added.
     * Only existing parameters defined in newParameters are affected.
     *
     * @param parameters
     * @param newParameters
     */
    public void mergeParameters(ProjectParameters parameters, ProjectParameters newParameters) {
        for (ProjectParameters.Parameter oldParameter : parameters.getParameters()) {
            List<ProjectParameters.Parameter> paramList = newParameters.getParameters();
            if (paramList != null) {
                for (ProjectParameters.Parameter param: paramList) {
                    if (oldParameter.getName().equals(param.getName())) {
                        oldParameter.setValue(param.getValue());
                    }
                }
            }
        }
    }
}
