package com.gitittogether.skillForge.shared.utils;

public enum Language {
    EN("English"),
    ES("Spanish"),
    FR("French"),
    DE("German"),
    ZH("Chinese"),
    JA("Japanese"),
    RU("Russian"),
    PT("Portuguese"),
    AR("Arabic"),
    HI("Hindi");

    private final String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return name().toLowerCase();
    }
} 