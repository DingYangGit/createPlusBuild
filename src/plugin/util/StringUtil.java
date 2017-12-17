package plugin.util;

public class StringUtil {
    public static String toLowerStanding(String string) {
        return Character.toLowerCase(string.charAt(0)) +
                string.substring((1));
    }
}
