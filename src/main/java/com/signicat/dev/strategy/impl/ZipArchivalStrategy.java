package com.signicat.dev.strategy.impl;

import com.signicat.dev.enums.ArchiveType;
import com.signicat.dev.strategy.ArchivalStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ZipArchivalStrategy implements ArchivalStrategy {
    private static final Logger log = LoggerFactory.getLogger(ZipArchivalStrategy.class);
    private static final int BUFFER_SIZE = 8192;

    /**
     * @param files The files to be archived
     * @return The archived files
     * @throws IOException If an I/O error occurs
     */
    public ByteArrayOutputStream archiveFiles(List<MultipartFile> files) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
            for (MultipartFile file : files) {
                ZipEntry zipEntry = new ZipEntry(file.getOriginalFilename());
                zipOut.putNextEntry(zipEntry);

                try (InputStream inputStream = file.getInputStream();
                     ReadableByteChannel inputChannel = Channels.newChannel(inputStream)) {
                    WritableByteChannel outputChannel = Channels.newChannel(zipOut);
                    ByteBuffer buffer = ByteBuffer.allocateDirect(8192);
                    while (inputChannel.read(buffer) != -1) {
                        buffer.flip();
                        outputChannel.write(buffer);
                        buffer.clear();
                    }
                } catch (IOException e) {
                    log.error("Error occurred while archiving file: {}", file.getOriginalFilename());
                    throw new IOException("Error occurred while archiving file: " + file.getOriginalFilename(), e);
                } finally {
                    zipOut.closeEntry();
                }
            }
        }
        return outputStream;
    }

    /**
     * @return
     */
    public ArchiveType getArchiveType() {
        return ArchiveType.ZIP;
    }
}
