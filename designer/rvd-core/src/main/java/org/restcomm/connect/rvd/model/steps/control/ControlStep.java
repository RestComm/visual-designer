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

package org.restcomm.connect.rvd.model.steps.control;

import org.restcomm.connect.rvd.model.project.BaseStep;
import java.util.List;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class ControlStep extends BaseStep {

    protected List<Condition> conditions;
    protected List<Action> actions;
    protected String conditionExpression;

    public static class Condition {

        public String name;
        public String operator;
        public Comparison comparison; // one of comparison/matcher is enabled at a time
        public Matcher matcher;

        public enum Operators {
            equals,
            greater,
            greaterEqual,
            less,
            lessEqual,
            matches

        }
        public static class Comparison {
            public String operand1;
            public String operand2;
            public ComparisonType type;

            public enum ComparisonType {
                text, numeric
            }

        }
        public static class Matcher {
            public String text;
            public String regex;

        }
    }

    public static class Action {
        public String name;
        public AssignParams assign;
        public ContinueToParams continueTo;
        public CaptureParams capture;
    }

    public enum VariableScopes {
        mod,
        app
    }

    public static class AssignParams {
        public String expression;
        public String varName;
        public VariableScopes varScope;
    }

    public static class ContinueToParams {
        public String target;
    }

    public static class CaptureParams {
        public String regex;
        public String data;
        public String varName;
        public VariableScopes varScope;
    }
}
