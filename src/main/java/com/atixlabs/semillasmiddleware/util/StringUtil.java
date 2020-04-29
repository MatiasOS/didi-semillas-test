package com.atixlabs.semillasmiddleware.util;


import org.apache.commons.lang3.StringUtils;

public final class StringUtil {
    public static String toUpperCaseTrimAndRemoveAccents(String s) {
        return StringUtils.stripAccents(s.toUpperCase().trim());
    }

    public static String removeNumbers(String s){
        return s.replaceAll("[0123456789]", "").trim();
    }
}
