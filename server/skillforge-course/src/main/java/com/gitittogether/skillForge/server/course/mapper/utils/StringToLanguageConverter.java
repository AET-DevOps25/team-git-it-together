package com.gitittogether.skillForge.server.course.mapper.utils;

import com.gitittogether.skillForge.server.course.model.utils.Language;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToLanguageConverter implements Converter<String, Language> {
    @Override
    public Language convert(@NonNull String source) {
        return Language.valueOf(source.trim().toUpperCase());
    }
} 