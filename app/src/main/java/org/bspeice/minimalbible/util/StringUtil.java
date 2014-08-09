package org.bspeice.minimalbible.util;

/**
 * Created by bspeice on 8/3/14.
 */
public class StringUtil {

    // Convenience from http://stackoverflow.com/a/17795110/1454178
    public static String joinString(String join, Object... strings) {
        if (strings == null || strings.length == 0) {
            return "";
        } else if (strings.length == 1) {
            return strings[0].toString();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(strings[0]);
            for (int i = 1; i < strings.length; i++) {
                sb.append(join).append(strings[i].toString());
            }
            return sb.toString();
        }
    }
}
