package com.signicat.dev.service.storage;

import java.io.InputStream;

public interface FileStorageService {
    void storeFile(String fileName, InputStream fileContent);
}
