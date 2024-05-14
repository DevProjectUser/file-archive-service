package com.signicat.dev.service.archival.impl;

import com.signicat.dev.domain.entity.UploadStatistic;
import com.signicat.dev.enums.ArchiveType;
import com.signicat.dev.exception.ArchivalStrategyNotFoundException;
import com.signicat.dev.exception.FairUsageLimitExpiredException;
import com.signicat.dev.repository.UploadStatisticRepository;
import com.signicat.dev.service.archival.FileArchivalService;
import com.signicat.dev.strategy.ArchivalStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileArchivalServiceImpl implements FileArchivalService {

    private final Logger logger = LoggerFactory.getLogger(FileArchivalServiceImpl.class);
    private final UploadStatisticRepository uploadStatisticRepository;
    private final Map<ArchiveType, ArchivalStrategy> archivingStrategyMap;

    @Value("${archive.service.fair.usage.limit}")
    private int fairUsageLimit;


    @Transactional(rollbackFor = Exception.class)
    public ByteArrayOutputStream archiveFiles(List<MultipartFile> files, String archiveFormat, String ipAddress) throws FairUsageLimitExpiredException {
        ArchiveType archiveType = ArchiveType.fromValue(archiveFormat);
        ArchivalStrategy archivalStrategy = archivingStrategyMap.getOrDefault(archiveType, null);
        try {
            recordUploadStats(ipAddress, files.size());
            if (archivalStrategy == null) {
                logger.error("Archival strategy not found for {}", archiveFormat);
                throw new ArchivalStrategyNotFoundException(String.format("Archival format provided is not supported : %s", archiveFormat));
            }
            logger.info("Archiving files using {}", archivalStrategy.getArchiveType());
            return archivalStrategy.archiveFiles(files);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void recordUploadStats(String ipAddress, int fileCount) throws FairUsageLimitExpiredException {

        UploadStatistic uploadStatistic = uploadStatisticRepository.findByIpAddressAndDate(ipAddress, LocalDate.now());
        if (uploadStatistic != null) {
            int totalFileCount = uploadStatistic.getFileCount() + fileCount;
            if (totalFileCount >= fairUsageLimit) {
                logger.error("Fair usage limit exceeded for IP Address: {}", ipAddress);
                throw new FairUsageLimitExpiredException("Fair usage limit exceeded for IP Address for today : " + ipAddress);
            }
            uploadStatistic.setFileCount(totalFileCount);
            uploadStatisticRepository.save(uploadStatistic);
        } else {
            uploadStatistic = new UploadStatistic();
            uploadStatistic.setIpAddress(ipAddress);
            uploadStatistic.setDate(LocalDate.now());
            uploadStatistic.setFileCount(fileCount);
            uploadStatisticRepository.save(uploadStatistic);
        }

    }
}
