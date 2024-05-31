//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package thunder.hack.core.impl;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class StringHelperUpdated {
    private static final Pattern FORMATTING_CODE = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
    private static final Pattern LINE_BREAK = Pattern.compile("\\r\\n|\\v");
    private static final Pattern ENDS_WITH_LINE_BREAK = Pattern.compile("(?:\\r\\n|\\v)$");

    public StringHelperUpdated() {
    }

    public static String formatTicks(int ticks, float tickRate) {
        int i = MathHelper.floor((float)ticks / tickRate);
        int j = i / 60;
        i %= 60;
        int k = j / 60;
        j %= 60;
        return k > 0 ? String.format(Locale.ROOT, "%02d:%02d:%02d", k, j, i) : String.format(Locale.ROOT, "%02d:%02d", j, i);
    }

    public static String stripTextFormat(String text) {
        return FORMATTING_CODE.matcher(text).replaceAll("");
    }

    public static boolean isEmpty(@Nullable String text) {
        return StringUtils.isEmpty(text);
    }

    public static String truncate(String text, int maxLength, boolean addEllipsis) {
        if (text.length() <= maxLength) {
            return text;
        } else if (addEllipsis && maxLength > 3) {
            String var10000 = text.substring(0, maxLength - 3);
            return var10000 + "...";
        } else {
            return text.substring(0, maxLength);
        }
    }

    public static int countLines(String text) {
        if (text.isEmpty()) {
            return 0;
        } else {
            Matcher matcher = LINE_BREAK.matcher(text);

            int i;
            for(i = 1; matcher.find(); ++i) {
            }

            return i;
        }
    }

    public static boolean endsWithLineBreak(String text) {
        return ENDS_WITH_LINE_BREAK.matcher(text).find();
    }

    public static String truncateChat(String text) {
        return truncate(text, 256, false);
    }

    public static boolean isValidChar(char c) {
        return c != 167 && c >= ' ' && c != 127;
    }

    public static boolean isValidPlayerName(String name) {
        return name.length() > 16 ? false : name.chars().filter((c) -> {
            return c <= 32 || c >= 127;
        }).findAny().isEmpty();
    }

    public static String stripInvalidChars(String string) {
        return stripInvalidChars(string, false);
    }

    public static String stripInvalidChars(String string, boolean allowLinebreak) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] var3 = string.toCharArray();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            char c = var3[var5];
            if (isValidChar(c)) {
                stringBuilder.append(c);
            } else if (allowLinebreak && c == '\n') {
                stringBuilder.append(c);
            }
        }

        return stringBuilder.toString();
    }

    public static boolean isWhitespace(int c) {
        return Character.isWhitespace(c) || Character.isSpaceChar(c);
    }

    public static boolean isBlank(@Nullable String string) {
        return string != null && string.length() != 0 ? string.chars().allMatch(StringHelperUpdated::isWhitespace) : true;
    }
}
