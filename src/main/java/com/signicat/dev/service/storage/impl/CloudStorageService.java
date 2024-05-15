package com.signicat.dev.service.storage.impl;

import com.signicat.dev.service.storage.FileStorageService;

import java.io.InputStream;

public class CloudStorageService implements FileStorageService {
    /**
     * @param fileName
     * @param fileContent
     */
    public void storeFile(String fileName, InputStream fileContent) {
        //Implement file input stream storage to cloud.
    }

}
