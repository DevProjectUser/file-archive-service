package com.signicat.dev.enums;

import com.signicat.dev.exception.ArchivalStrategyNotFoundException;
import lombok.Getter;

@Getter
public enum ArchiveType {
    ZIP("zip"),
    TAR("tar"),
    GZIP("gz"),
    SEVEN_ZIP("7z");

    private final String extension;

    ArchiveType(String extension) {
        this.extension = extension;
    }

    public static ArchiveType fromValue(String value) {
        for (ArchiveType archiveType : ArchiveType.values()) {
            if (archiveType.getExtension().equalsIgnoreCase(value)) {
                return archiveType;
            }
        }
        throw new ArchivalStrategyNotFoundException("Archival format provided is not supported : " + value);
    }
}
