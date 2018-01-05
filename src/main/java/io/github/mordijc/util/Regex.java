package io.github.mordijc.util;

import java.util.regex.Matcher;

public class Regex {
    public static String getGroupOrEmptyString(Matcher matcher, String groupName) {
        try {
            return matcher.group(groupName);
        } catch (IllegalArgumentException e) {
            return "";
        }
    }
}
