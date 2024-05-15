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
import java.util.Optional;

import static com.signicat.dev.constants.ApplicationConstants.FAIR_USAGE_LIMIT_FOR_DAY;

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
    public ByteArrayOutputStream archiveFiles(List<MultipartFile> files, String archiveFormat, String ipAddress) {
        try {
            ArchiveType archiveType = ArchiveType.fromValue(archiveFormat);
            ArchivalStrategy archivalStrategy = archivingStrategyMap.get(archiveType);
            recordUploadStats(ipAddress, files.size());
            logger.info("Archiving files using {}", archivalStrategy.getArchiveType());
            return archivalStrategy.archiveFiles(files);
        } catch (Exception e) {
            switch (e.getClass().getSimpleName()) {
                case "ArchivalStrategyNotFoundException":
                    throw (ArchivalStrategyNotFoundException) e;
                case "FairUsageLimitExpiredException":
                    throw (FairUsageLimitExpiredException) e;
                default:
                    logger.error("Error occurred during file processing", e);
                    throw new RuntimeException(e.getMessage());
            }
        }
    }

    private void recordUploadStats(String ipAddress, int fileCount) {
        try {
            Optional<UploadStatistic> optionalUploadStatistic = uploadStatisticRepository.findByIpAddressAndDate(ipAddress, LocalDate.now());

            UploadStatistic uploadStatistic = optionalUploadStatistic.orElseGet(() -> {
                UploadStatistic newUploadStatistic = new UploadStatistic();
                newUploadStatistic.setIpAddress(ipAddress);
                newUploadStatistic.setDate(LocalDate.now());
                return newUploadStatistic;
            });

            int currentFileCount = uploadStatistic.getFileCount();
            int fileCountRemaining = fairUsageLimit - currentFileCount;
            if (fileCount > fileCountRemaining) {
                logger.error("Fair usage limit exceeded for IP Address: {}", ipAddress);
                String message = String.format(FAIR_USAGE_LIMIT_FOR_DAY, ipAddress, fileCountRemaining, fairUsageLimit);
                throw new FairUsageLimitExpiredException(message);
            }

            uploadStatistic.setFileCount(currentFileCount + fileCount);
            uploadStatisticRepository.save(uploadStatistic);
        } catch (FairUsageLimitExpiredException e) {
            throw new FairUsageLimitExpiredException(e.getMessage());
        } catch (Exception e) {
            logger.error("Error occurred while saving upload stats to database", e);
            throw new RuntimeException("Error occurred while recording upload stats", e);
        }
    }
}
