package org.restcomm.connect.rvd.project;

import java.util.Date;
import java.util.List;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class ProjectUtils {

    public static ProjectKind guessProjectKindFromTemplateTags(List<String> templateTags) {
        if (templateTags == null || templateTags.size() == 0)
            return null;

        if (templateTags.contains("voice"))
            return ProjectKind.voice;
        if (templateTags.contains("sms"))
            return ProjectKind.sms;
        if (templateTags.contains("ussd")) {
            return ProjectKind.ussd;
        }

        return null;
    }

    public static String generateUniqueFriendlyName(String optionalBase) {
        if (optionalBase == null)
            optionalBase = "project ";

        String name = optionalBase + (new Date().getTime());
        return name;
    }
}
