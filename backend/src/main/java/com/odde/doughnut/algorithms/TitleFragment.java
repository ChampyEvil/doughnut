package com.odde.doughnut.algorithms;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class TitleFragment {
    final static String internalPartialMatchReplacement = "__p_a_r_t_i_a_l__";
    final static String internalFullMatchReplacement = "__f_u_l_l__";
    final static String internalPartialMatchReplacementForSubtitle = "__p_a_r_t_i_a_l_s_u_b__";
    final static String internalFullMatchReplacementForSubtitle = "__f_u_l_l_s_u_b__";
    private final String content;
    private final boolean suffix;
    private boolean subtitle;

    TitleFragment(String content, boolean subtitle) {
        this.subtitle = subtitle;
        String trimmed = content.trim();
        if(content.startsWith("~")) {
            this.content = trimmed.substring(1);
            this.suffix = true;
        }
        else {
            this.content = trimmed;
            this.suffix = false;
        }
    }

    static String replaceMasks(String titleMasked, ClozeReplacement clozeReplacement) {
        return titleMasked
                .replace(internalFullMatchReplacement, clozeReplacement.fullMatchReplacement)
                .replace(internalPartialMatchReplacement, clozeReplacement.partialMatchReplacement)
                .replace(internalFullMatchReplacementForSubtitle, clozeReplacement.fullMatchSubtitleReplacement)
                .replace(internalPartialMatchReplacementForSubtitle, clozeReplacement.partialMatchSubtitleReplacement);
    }

    String clozeIt(String description) {
        return replaceSimilar(replaceLiteralWords(description));
    }

    boolean matches(String answer) {
        return content.equalsIgnoreCase(answer);
    }

    private String replaceSimilar(String literal) {
        if (content.length() < 4) {
            return literal;
        }
        String substring = content.substring(0, (content.length() + 1) * 3 / 4);
        Pattern pattern = Pattern.compile(Pattern.quote(substring), Pattern.CASE_INSENSITIVE);
        return pattern.matcher(literal).replaceAll(getInternalPartialMatchReplacement());
    }

    private String getPatternStringForLiteralMatch() {
        if (content.length() >= 4 || suffix) {
            String ignoreConjunctions = String.join("([\\s-]+)((and\\s+)|(the\\s+)|(a\\s+)|(an\\s+))?",
                    Arrays.stream(content.split("[\\s-]+"))
                            .filter(x -> !Arrays.asList("the", "a", "an").contains(x))
                            .map(Pattern::quote).collect(Collectors.toUnmodifiableList()));
            return suffixIfNeeded(ignoreConjunctions);
        }
        if (content.matches("^\\d+$")) {
            return "(?<!\\d)" + Pattern.quote(content) + "(?!\\d)";
        }
        return "(?<!\\w)" + Pattern.quote(content) + "(?!\\w)";
    }

    private String suffixIfNeeded(String pattern) {
        if(suffix) {
            return "(?U)(?<=\\p{Alnum})" + pattern;
        }
        return pattern;
    }

    private String replaceLiteralWords(String description) {
        Pattern pattern = Pattern.compile(getPatternStringForLiteralMatch(), Pattern.CASE_INSENSITIVE);
        return pattern.matcher(description).replaceAll(getInternalFullMatchReplacement());
    }

    private String getInternalFullMatchReplacement() {
        if(subtitle) return internalFullMatchReplacementForSubtitle;
        return internalFullMatchReplacement;
    }

    private String getInternalPartialMatchReplacement() {
        if(subtitle) return internalPartialMatchReplacementForSubtitle;
        return internalPartialMatchReplacement;
    }

}
