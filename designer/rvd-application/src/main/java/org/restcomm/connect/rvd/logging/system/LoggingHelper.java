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

package org.restcomm.connect.rvd.logging.system;

import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class LoggingHelper {
    public static String buildMessage(Class loggingClass, String methodName, String prefix, String message) {
        return MessageFormat.format("[{0}:{1}] {2} {3}", StringUtils.defaultIfBlank(loggingClass.getSimpleName(),""), StringUtils.defaultIfBlank(methodName,""), StringUtils.defaultIfBlank(prefix,""), StringUtils.defaultIfBlank(message,""));
    }

    public static String buildMessage(String prefix, String message) {
        return MessageFormat.format("{0} {1}", prefix, message);
    }

    public static String buildMessage(Class loggingClass, String methodName, String pattern, Object[] items) {
        StringBuffer buffer = new StringBuffer("[").append(StringUtils.defaultIfBlank(loggingClass.getSimpleName(),"")).append(":").append(StringUtils.defaultIfBlank(methodName,"")).append("]");
        return new MessageFormat(pattern).format(items,buffer,null).toString();
    }

    public static String buildMessage(Class loggingClass, String methodName, String message) {
        return MessageFormat.format("[{0}:{1}] {2}", loggingClass.getSimpleName(), methodName, message);
    }

}
