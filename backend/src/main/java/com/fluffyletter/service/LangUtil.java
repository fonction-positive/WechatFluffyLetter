package com.fluffyletter.service;

public class LangUtil {

    private LangUtil() {
    }

    public static String normalize(String lang) {
        if (lang == null) return "zh";
        String l = lang.trim().toLowerCase();
        if (l.startsWith("en")) return "en";
        return "zh";
    }
}
