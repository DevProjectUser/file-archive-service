package com.signicat.dev.strategy;

import com.signicat.dev.enums.ArchiveType;

public interface ArchivalStrategy {
    void archiveFiles();

    ArchiveType getArchiveType();
}
