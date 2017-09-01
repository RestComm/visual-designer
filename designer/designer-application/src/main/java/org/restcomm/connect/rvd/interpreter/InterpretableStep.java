/*
 *
 *  * TeleStax, Open Source Cloud Communications
 *  * Copyright 2016, Telestax Inc and individual contributors
 *  * by the @authors tag.
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * under the terms of the GNU Affero General Public License as
 *  * published by the Free Software Foundation; either version 3 of
 *  * the License, or (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU Affero General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU Affero General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *  *
 *
 */

/**
 * Adds processing and rendering bahavior on top of the BaseStep class.
 *
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
package org.restcomm.connect.rvd.interpreter;

import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import javax.servlet.http.HttpServletRequest;


/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public interface InterpretableStep extends Interpretable {

    void handleAction(Interpreter interpreter, Target originTarget) throws InterpreterException, StorageException;

    /**
     * @returns String - The module name to continue rendering with. null, to continue processing the existing module
     */
    String process(Interpreter interpreter, HttpServletRequest httpRequest) throws InterpreterException;

}
