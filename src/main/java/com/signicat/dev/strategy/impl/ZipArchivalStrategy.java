package com.signicat.dev.strategy.impl;

import com.signicat.dev.enums.ArchiveType;
import com.signicat.dev.strategy.ArchivalStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ZipArchivalStrategy implements ArchivalStrategy {
    private static final Logger log = LoggerFactory.getLogger(ZipArchivalStrategy.class);

    /**
     *
     */
    public void archiveFiles() {
        log.info("ZipArchivalStrategy.archiveFiles");
    }

    /**
     * @return
     */
    public ArchiveType getArchiveType() {
        return ArchiveType.ZIP;
    }
}
