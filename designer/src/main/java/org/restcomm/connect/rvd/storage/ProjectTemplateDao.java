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

package org.restcomm.connect.rvd.storage;

import org.restcomm.connect.rvd.http.PaginatedResults;
import org.restcomm.connect.rvd.model.ProjectTemplate;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;


/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public interface ProjectTemplateDao {

    /**
     * Loads and returns a project template entity
     *
     * It will throw a StorageEntityNotFound in case this is not found (wrong id or bad templates path in configuration)
     *
     * @param id
     * @return a ProjectTemplate object
     * @throws StorageException
     */
    ProjectTemplate loadProjectTemplate(String id) throws StorageException;

    /**
     * Get all available project templates
     *
     * @return
     * @param pageIndex
     * @param pageSize
     * @param sortingCriteria
     * @param results
     */
    void loadProjectTemplates(Integer pageIndex, Integer pageSize, String sortingCriteria, PaginatedResults<ProjectTemplate> results) throws StorageException;
}
