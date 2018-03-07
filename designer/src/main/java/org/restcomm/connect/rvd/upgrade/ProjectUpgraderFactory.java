package org.restcomm.connect.rvd.upgrade;

import org.restcomm.connect.rvd.upgrade.exceptions.NoUpgraderException;

public class ProjectUpgraderFactory {

    public static ProjectUpgrader create(String fromVersion) throws NoUpgraderException {
        if  ("1.14".equals(fromVersion)) {
            return new ProjectUpgrader114to115();
        } else
        if  ("1.13".equals(fromVersion)) {
            return new ProjectUpgrader113to114();
        } else
        if  ("1.12".equals(fromVersion)) {
            return new ProjectUpgrader112to113();
        } else
        if  ("1.11".equals(fromVersion)) {
            return new ProjectUpgrader111to112();
        } else
        if  ("1.10".equals(fromVersion)) {
            return new ProjectUpgrader110to111();
        } else
        if  ("1.9".equals(fromVersion)) {
            return new ProjectUpgrader19to110();
        } else
        if  ("1.8".equals(fromVersion)) {
            return new ProjectUpgrader18to19();
        } else
        if  ("1.7".equals(fromVersion)) {
            return new ProjectUpgrader17to18();
        } else
        if ( "rvd714".equals(fromVersion) ) {
            return new ProjectUpgrader714To10();
        } else
        if  ("1.0".equals(fromVersion)) {
            return new ProjectUpgrader10to11();
        } else
        if  ("1.1".equals(fromVersion)) {
            return new ProjectUpgraded11to12();
        } else
        if  ("1.2".equals(fromVersion)) {
            return new ProjectUpgrader12to13();
        } else
        if  ("1.3".equals(fromVersion)) {
            return new ProjectUpgrader13to14();
        } else
        if  ("1.4".equals(fromVersion)) {
            return new ProjectUpgrader14to15();
        } else
        if  ("1.5".equals(fromVersion)) {
            return new ProjectUpgrader15to16();
        } else
        if  ("1.6".equals(fromVersion)) {
            return new ProjectUpgrader16to17();
        } else
            throw new NoUpgraderException("No project upgrader found for project with version " + fromVersion);
    }
}
