package org.wolkenproject.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Logger {
    public static void faterr(String msg)
    {
        System.err.println(msg);
    }

    public static void alert(String msg, Object ...arguments) {
        Pattern pattern = Pattern.compile("\\$\\{[A-z]\\}");
        Matcher matcher = pattern.matcher(msg);
        int index       = 0;

        while (matcher.find()) {
            msg = matcher.replaceFirst(Utils.toString(arguments[index ++]));
            matcher = pattern.matcher(msg);
        }

        System.out.println(msg);
        System.out.println();
    }
}
