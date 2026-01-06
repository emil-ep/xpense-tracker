package com.xperia.xpense_tracker;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class DescriptionNormaliser {

    private static final Pattern NON_ALPHA_NUM =
            Pattern.compile("[^a-z0-9 ]");

    private static final Pattern MULTI_SPACE =
            Pattern.compile("\\s+");

    public static String normalize(String input) {
        if (input == null) return "";

        // 1. Unicode normalize
        String s = Normalizer.normalize(input, Normalizer.Form.NFKC);

        // 2. Lowercase
        s = s.toLowerCase(Locale.ENGLISH);

        // 3. Replace non-alphanumeric with space
        s = NON_ALPHA_NUM.matcher(s).replaceAll(" ");

        // 4. Collapse spaces
        s = MULTI_SPACE.matcher(s).replaceAll(" ").trim();

        return s;
    }
}
