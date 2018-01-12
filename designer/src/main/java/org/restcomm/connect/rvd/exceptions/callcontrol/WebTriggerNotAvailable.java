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

package org.restcomm.connect.rvd.exceptions.callcontrol;

/**
 * Thrown when WebTrigger functionality is either not enables or does not apply to
 * a specific type of project.
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class WebTriggerNotAvailable extends CallControlException {

    public WebTriggerNotAvailable(String message) {
        super(message);
    }

    public WebTriggerNotAvailable(String message, Throwable cause) {
        super(message, cause);
    }
}
