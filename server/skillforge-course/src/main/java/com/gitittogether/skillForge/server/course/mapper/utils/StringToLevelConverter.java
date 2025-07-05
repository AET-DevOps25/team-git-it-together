package com.gitittogether.skillForge.server.course.mapper.utils;

import com.gitittogether.skillForge.server.course.model.utils.Level;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToLevelConverter implements Converter<String, Level> {
    @Override
    public Level convert(@NonNull String source) {
        return Level.valueOf(source.trim().toUpperCase());
    }
} 